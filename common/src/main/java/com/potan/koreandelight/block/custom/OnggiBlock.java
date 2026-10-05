package com.potan.koreandelight.block.custom;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import com.potan.koreandelight.menu.OnggiMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
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
            boolean hasLid = !state.getValue(HAS_LID);
            level.setBlock(pos, state.setValue(HAS_LID, hasLid), 3);
            level.playSound(null, pos, hasLid ? net.minecraft.sounds.SoundEvents.BARREL_CLOSE : net.minecraft.sounds.SoundEvents.BARREL_OPEN,
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.7F, 0.9F);
        } else if (!state.getValue(HAS_LID)) {
            player.openMenu(new SimpleMenuProvider(
                    (id, inventory, menuPlayer) -> new OnggiMenu(id, inventory, onggi),
                    Component.translatable("block.koreandelight.onggi_block")));
        } else {
            player.displayClientMessage(Component.translatable("block.koreandelight.onggi.lid_closed"), true);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (heldItem.isEmpty() || player.isShiftKeyDown()) {
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
        if (OnggiBlockEntity.isFluidContainer(heldItem)) {
            net.minecraft.world.SimpleContainer buckets = new net.minecraft.world.SimpleContainer(OnggiBlockEntity.TOTAL_SLOTS);
            buckets.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, heldItem.copyWithCount(1));
            var result = OnggiBlockEntity.transferFluidContainer(buckets, onggi.getStoredFluid(), onggi.getStoredFluidAmount());
            if (result != null && (result.fluid() != onggi.getStoredFluid() || result.amount() != onggi.getStoredFluidAmount())) {
                onggi.setStoredFluid(result.fluid(), result.amount());
                player.setItemInHand(hand, net.minecraft.world.item.ItemUtils.createFilledResult(heldItem, player,
                        buckets.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT)));
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
