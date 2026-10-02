package com.potan.koreandelight.block.custom;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class OnggiBlock extends Block implements EntityBlock {
    public static final BooleanProperty HAS_LID = BooleanProperty.create("has_lid");
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);

    public OnggiBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_LID, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_LID);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get().create(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof OnggiBlockEntity onggi)) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            if (!state.getValue(HAS_LID)) {
                ItemStack extracted = onggi.extractItem();
                if (!extracted.isEmpty() && !player.getInventory().add(extracted)) {
                    player.drop(extracted, false);
                }
            }
        } else {
            boolean hasLid = !state.getValue(HAS_LID);
            level.setBlock(pos, state.setValue(HAS_LID, hasLid), 3);
            level.playSound(null, pos, hasLid ? net.minecraft.sounds.SoundEvents.BARREL_CLOSE : net.minecraft.sounds.SoundEvents.BARREL_OPEN,
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.7F, 0.9F);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (heldItem.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        // Consume failed interactions too, so buckets cannot spill beside a closed/full jar.
        if (state.getValue(HAS_LID)) {
            if (!level.isClientSide) {
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable("block.koreandelight.onggi.lid_closed"), true);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof OnggiBlockEntity onggi)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (heldItem.is(net.minecraft.world.item.Items.BUCKET)) {
            if (onggi.getStoredFluidAmount() >= 1000 && onggi.getStoredFluid().getBucket() != net.minecraft.world.item.Items.AIR) {
                ItemStack filledBucket = new ItemStack(onggi.getStoredFluid().getBucket());
                player.setItemInHand(hand, net.minecraft.world.item.ItemUtils.createFilledResult(heldItem, player, filledBucket));
                onggi.setStoredFluid(net.minecraft.world.level.material.Fluids.EMPTY, 0);
            }
        } else if (heldItem.getItem() instanceof net.minecraft.world.item.BucketItem) {
            net.minecraft.world.level.material.Fluid fluid = net.minecraft.world.level.material.Fluids.EMPTY;
            if (heldItem.is(net.minecraft.world.item.Items.WATER_BUCKET)) {
                fluid = net.minecraft.world.level.material.Fluids.WATER;
            } else if (heldItem.is(com.potan.koreandelight.item.ModItems.SOY_SAUCE_BUCKET.get())) {
                fluid = com.potan.koreandelight.fluid.ModFluids.SOURCE_SOY_SAUCE.get();
            }
            if (fluid != net.minecraft.world.level.material.Fluids.EMPTY && onggi.getStoredFluidAmount() == 0) {
                onggi.setStoredFluid(fluid, 1000);
                player.setItemInHand(hand, net.minecraft.world.item.ItemUtils.createFilledResult(heldItem, player,
                        new ItemStack(net.minecraft.world.item.Items.BUCKET)));
            }
        } else if (onggi.insertItem(heldItem) && !player.isCreative()) {
            heldItem.shrink(1);
        }
        return ItemInteractionResult.CONSUME;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get(), OnggiBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof OnggiBlockEntity onggi) {
                Containers.dropContents(level, pos, onggi.getInventory());
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> type1, BlockEntityType<E> type2, BlockEntityTicker<E> ticker) {
        return type2 == type1 ? (BlockEntityTicker<A>) ticker : null;
    }
}
