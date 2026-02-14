package com.potan.koreandelight.block.custom;

import com.potan.koreandelight.block.blockentity.KimjangBasinEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 김장 대야 블록 클래스입니다.
 * 2x2 크기를 차지하며, 설치 시 주변 공간을 확인합니다.
 */
public class KimjangBasin extends Block implements EntityBlock {
    public static final EnumProperty<TrayPart> PART = EnumProperty.create("part", TrayPart.class);
    
    // 대야의 기본 모양 (간단하게 설정, 필요시 정교화 가능)
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    public KimjangBasin(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // 마스터 파트(NORTH_WEST)인 경우에만 블록 엔티티를 생성합니다.
        if (state.getValue(PART) == TrayPart.NORTH_WEST) {
            return new KimjangBasinEntity(pos, state);
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        // 2x2 영역(현재 위치 포함 동쪽, 남쪽, 남동쪽)이 배치 가능한지 확인합니다.
        for (TrayPart part : TrayPart.values()) {
            BlockPos targetPos = pos.offset(part.getOffset());
            if (!level.getBlockState(targetPos).canBeReplaced(context)) {
                // 공간이 없으면 null을 반환하여 아이템이 소비되지 않고 설치되지 않게 합니다.
                return null;
            }
        }

        // 기본적으로 북서쪽(North West) 파트로 시작합니다.
        return this.defaultBlockState().setValue(PART, TrayPart.NORTH_WEST);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            // 마스터 블록(NW) 외의 나머지 3개 위치에 블록을 배치합니다.
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
            // 한 파트가 제거되면 나머지 모든 파트도 함께 제거합니다.
            TrayPart part = state.getValue(PART);
            BlockPos masterPos = getMasterPos(pos, part);

            for (TrayPart p : TrayPart.values()) {
                BlockPos target = getPosFromMaster(masterPos, p);
                if (level.getBlockState(target).is(this)) {
                    // 아이템 드롭 없이 블록만 제거 (원래 부서진 블록에서만 아이템이 나옵니다)
                    level.removeBlock(target, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private BlockPos getPosFromMaster(BlockPos masterPos, TrayPart p) {
        BlockPos offset = p.getOffset();
        return masterPos.offset(offset);
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
