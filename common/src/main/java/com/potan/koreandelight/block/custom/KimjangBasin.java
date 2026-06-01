package com.potan.koreandelight.block.custom;

import com.potan.koreandelight.block.blockentity.KimjangBasinEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 김장 대야 블록 클래스입니다.
 * 2x2 크기를 차지하며, 설치 시 주변 공간을 확인합니다.
 */
public class KimjangBasin extends Block implements EntityBlock {
    public static final EnumProperty<TrayPart> PART = EnumProperty.create("part", TrayPart.class);
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    public KimjangBasin(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * 1.21.1 사양: 플레이어가 빈손으로 김장 대야를 우클릭했을 때 호출됩니다.
     * 대야에 담긴 채소와 양념 재료가 매칭된다면 김장을 완료하여 완성된 김치 아이템을 지급합니다.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // 클릭한 부위의 상태값에 근거하여 항상 북서쪽 마스터 블록 엔티티 위치로 리디렉션합니다.
        TrayPart part = state.getValue(PART);
        BlockPos masterPos = getMasterPos(pos, part);
        
        BlockEntity entity = level.getBlockEntity(masterPos);
        if (entity instanceof KimjangBasinEntity basinEntity) {
            // 맨손 우클릭 시 김장 프로세스를 수행합니다.
            if (!level.isClientSide && basinEntity.performKimjang(player, level)) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    /**
     * 1.21.1 사양: 플레이어가 손에 재료 아이템을 든 채 김장 대야를 우클릭했을 때 호출됩니다.
     * 손에 든 아이템이 김장 채소 또는 김장 양념이라면 대야의 빈 슬롯에 아이템을 1개 투입합니다.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 클릭한 부위의 상태값에 근거하여 항상 북서쪽 마스터 블록 엔티티 위치로 리디렉션합니다.
        TrayPart part = state.getValue(PART);
        BlockPos masterPos = getMasterPos(pos, part);
        
        BlockEntity entity = level.getBlockEntity(masterPos);
        if (entity instanceof KimjangBasinEntity basinEntity) {
            // 들고 있는 아이템이 존재할 경우 대야에 재료 추가를 시도합니다.
            if (!heldItem.isEmpty()) {
                if (!level.isClientSide && basinEntity.addIngredient(player, heldItem, level)) {
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(PART) == TrayPart.NORTH_WEST) {
            return new KimjangBasinEntity(pos, state);
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        for (TrayPart part : TrayPart.values()) {
            BlockPos targetPos = pos.offset(part.getOffset());
            if (!level.getBlockState(targetPos).canBeReplaced(context)) {
                return null;
            }
        }

        return this.defaultBlockState().setValue(PART, TrayPart.NORTH_WEST);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            for (TrayPart part : TrayPart.values()) {
                if (part == TrayPart.NORTH_WEST) continue;
                BlockPos targetPos = pos.offset(part.getOffset());
                level.setBlock(targetPos, state.setValue(PART, part), 3);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TrayPart part = state.getValue(PART);
            BlockPos masterPos = getMasterPos(pos, part);

            for (TrayPart p : TrayPart.values()) {
                BlockPos target = getPosFromMaster(masterPos, p);
                if (level.getBlockState(target).is(this)) {
                    level.removeBlock(target, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private BlockPos getPosFromMaster(BlockPos masterPos, TrayPart p) {
        return masterPos.offset(p.getOffset());
    }

    private BlockPos getMasterPos(BlockPos pos, TrayPart part) {
        return switch (part) {
            case NORTH_WEST -> pos;
            case NORTH_EAST -> pos.west();
            case SOUTH_WEST -> pos.north();
            case SOUTH_EAST -> pos.north().west();
        };
    }
}
