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
    public static IntegerProperty AGE = IntegerProperty.create("age", 0, 4);

    public MejuBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(AGE, 0));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {return true;}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
        super.createBlockStateDefinition(builder);
    }

    public int getMaxAge() {return 4;}

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isClientSide) {
            return;
        }
            int age = state.getValue(AGE);
        if (age < getMaxAge()) {

            // 기본값 20%, 주변에 빈 블록이 많을수록 증가
            float chance = 0.2f;
            for (BlockPos neighborPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                // 상하좌우가 빈 블록인지 확인
                if (level.isEmptyBlock(neighborPos)) {
                    chance += 0.1f;
                }
            }

            // 확률에 따라 성장
            if (level.getRandom().nextFloat() < chance) {
                level.setBlock(pos, state.setValue(AGE, age+1), 3);
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {return true; }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return getMaxAge() + 1 - blockState.getValue(AGE);
    }
}
