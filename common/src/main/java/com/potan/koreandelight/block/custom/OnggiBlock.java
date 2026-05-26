package com.potan.koreandelight.block.custom;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
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

    // 1.21.1에서는 useItemOn을 사용하여 아이템 상호작용을 처리합니다.
    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 쉬프트 + 우클릭으로 뚜껑을 여닫습니다.
        if (player.isShiftKeyDown()) {
            level.setBlock(pos, state.setValue(HAS_LID, !state.getValue(HAS_LID)), 3);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        // 뚜껑이 닫혀있으면 상호작용 불가
        if (state.getValue(HAS_LID)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof OnggiBlockEntity onggi)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        ItemStack outputItem = onggi.getInventory().getItem(0);

        // 플레이어가 빈손인 경우 아이템을 꺼냄
        if (heldItem.isEmpty()) {
            if (!outputItem.isEmpty()) {
                ItemStack extracted = onggi.getInventory().removeItem(0, 64);
                if (!player.getInventory().add(extracted)) {
                    player.drop(extracted, false);
                }
                onggi.setChanged();
                return ItemInteractionResult.SUCCESS;
            }
        } 
        // 아이템을 넣는 경우
        else {
            if (!outputItem.isEmpty()) {
                return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            }
            
            ItemStack toInsert = heldItem.copy();
            toInsert.setCount(1);
            onggi.getInventory().setItem(0, toInsert);
            heldItem.shrink(1);
            onggi.setChanged();
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get(), OnggiBlockEntity::tick);
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
