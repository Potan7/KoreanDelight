package com.potan.koreandelight.block.custom;

import com.potan.koreandelight.block.blockentity.KimjangBasinEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    
    // 대야의 기본 모양
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    public KimjangBasin(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 마스터 블록으로 리디렉션
        TrayPart part = state.getValue(PART);
        BlockPos masterPos = getMasterPos(pos, part);
        
        if (level.getBlockEntity(masterPos) instanceof KimjangBasinEntity basinEntity) {
            ItemStack stackInHand = player.getItemInHand(hand);

            // 맨손 우클릭 시 김장 진행
            if (stackInHand.isEmpty() && hand == InteractionHand.MAIN_HAND) {
                if (!level.isClientSide && basinEntity.performKimjang(player, level)) {
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            // 아이템이 있으면 다라이에 추가 시도
            if (!stackInHand.isEmpty()) {
                if (!level.isClientSide && basinEntity.addIngredient(player, stackInHand, level)) {
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.SUCCESS;
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
