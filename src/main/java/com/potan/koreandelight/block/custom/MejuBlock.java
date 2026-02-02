package com.potan.koreandelight.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class MejuBlock extends Block {
    public static IntegerProperty AGE = IntegerProperty.create("age", 0, 7);

    public MejuBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(AGE, 0));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {return true;}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE});
        super.createBlockStateDefinition(builder);
    }

    public int getMaxAge() {return 7;}

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isClientSide) {
            return;
        }

        int emptyNeighborCount = 0;
        for (BlockPos neighborPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            // 상하좌우가 빈 블록인지 확인
            if (level.isEmptyBlock(neighborPos)) {
                emptyNeighborCount++;
                break;
            }
        }

        int age = state.getValue(AGE);
        int nextAge = age + 1;
        if (age < this.getMaxAge()) {
            // 만약 빈 이웃 블록이 3개 초과라면, 발효 속도 증가
            if (emptyNeighborCount > 3 && nextAge < this.getMaxAge()) {
                nextAge++;
            }
            level.setBlock(pos, state.setValue(AGE, nextAge), 3);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {return true;    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return getMaxAge() + 1 - blockState.getValue(AGE);
    }
}
