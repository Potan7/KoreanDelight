package com.potan.koreandelight.block.custom;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.item.ModFoodItems;
import com.potan.koreandelight.item.SsamLeafItem;
import com.potan.koreandelight.block.blockentity.RiceBowlBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class RiceBowlBlock extends Block implements EntityBlock {
    public static final int DISH_SERVINGS = 5;
    public static final IntegerProperty SERVINGS = IntegerProperty.create("servings", 1, DISH_SERVINGS);
    public static final VoxelShape DEFAULT_SHAPE = Block.box(4.8, 0, 4.8, 11.2, 4, 11.2);
    private final VoxelShape shape;

    public RiceBowlBlock(Properties properties) {
        this(properties, DEFAULT_SHAPE);
    }

    public RiceBowlBlock(Properties properties, VoxelShape shape) {
        this(properties, shape, 1);
    }

    public RiceBowlBlock(Properties properties, VoxelShape shape, int servings) {
        super(properties.dynamicShape());
        this.shape = shape;
        registerDefaultState(stateDefinition.any().setValue(SERVINGS, servings));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SERVINGS);
    }

    public static ItemStack pickupStack(BlockState state) {
        if (!canPickup(state)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(state.getBlock());
        stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(SERVINGS, state.getValue(SERVINGS)));
        return stack;
    }

    public static boolean canPickup(BlockState state) {
        return state.getValue(SERVINGS) == state.getBlock().defaultBlockState().getValue(SERVINGS);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (!canPickup(state)) {
            Item container = asItem().getCraftingRemainingItem();
            return container == null ? List.of() : List.of(new ItemStack(container));
        }
        return super.getDrops(state, params);
    }

    public static boolean consumeServing(Level level, BlockPos pos, BlockState state, BlockState remainder) {
        Vec3 offset = level.getBlockEntity(pos) instanceof RiceBowlBlockEntity bowl
                ? bowl.getPlacementOffset() : Vec3.ZERO;
        if (!level.setBlock(pos, afterServing(state, remainder), 3)) {
            return false;
        }
        if (level.getBlockEntity(pos) instanceof RiceBowlBlockEntity bowl) {
            bowl.setPlacementLocation(Vec3.atLowerCornerOf(pos).add(0.5 + offset.x, 0, 0.5 + offset.z));
        }
        return true;
    }

    public static BlockState afterServing(BlockState state, BlockState remainder) {
        int servings = state.getValue(SERVINGS);
        return servings > 1 ? state.setValue(SERVINGS, servings - 1) : remainder;
    }

    public static Item dishLeaf(BlockState state) {
        if (state.is(ModBlocks.LETTUCE_BOWL.get())) {
            return ModFoodItems.LETTUCE.get();
        }
        return state.is(ModBlocks.PERILLA_LEAF_BOWL.get()) ? ModFoodItems.PERILLA_LEAF.get() : null;
    }

    public static boolean canTakeLeaf(ItemStack held, Item leaf) {
        return held.isEmpty() || (held.is(leaf) && SsamLeafItem.getIngredients(held) == 0
                && held.getCount() < held.getMaxStackSize());
    }

    public static InteractionResult takeLeaf(BlockState state, Level level, BlockPos pos, Player player) {
        Item leaf = dishLeaf(state);
        ItemStack held = player.getMainHandItem();
        if (leaf == null || !player.mayBuild() || !canTakeLeaf(held, leaf)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            if (!consumeServing(level, pos, state, Blocks.AIR.defaultBlockState())) {
                return InteractionResult.FAIL;
            }
            if (held.isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(leaf));
            } else {
                held.grow(1);
            }
            if (state.getValue(SERVINGS) == 1) {
                ItemStack bowl = new ItemStack(Items.BOWL);
                if (!player.getInventory().add(bowl)) {
                    player.drop(bowl, false);
                }
            }
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public VoxelShape getCenteredShape() {
        return shape;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.RICE_BOWL.get().create(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof RiceBowlBlockEntity bowl) {
            Vec3 offset = bowl.getPlacementOffset();
            return shape.move(offset.x, 0, offset.z);
        }
        return shape;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getMainHandItem().isEmpty() || !player.mayBuild()) {
            return InteractionResult.PASS;
        }
        if (dishLeaf(state) != null && !player.isShiftKeyDown()) {
            return takeLeaf(state, level, pos, player);
        }
        if (!canPickup(state)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.koreandelight.bowl.used"), true);
            }
            return InteractionResult.FAIL;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack pickedUp = pickupStack(state);
        if (!level.removeBlock(pos, false)) {
            return InteractionResult.FAIL;
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, pickedUp);
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.6F, 1.0F);
        level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        return InteractionResult.CONSUME;
    }
}
