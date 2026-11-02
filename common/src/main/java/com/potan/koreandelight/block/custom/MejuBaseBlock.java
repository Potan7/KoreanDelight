package com.potan.koreandelight.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MejuBaseBlock extends LanternBlock {
    private static final VoxelShape STANDING_SHAPE = Block.box(5, 0, 5, 11, 10, 11);
    private static final VoxelShape HANGING_SHAPE = Block.box(5, 1, 5, 11, 16, 11);

    public MejuBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HANGING) ? HANGING_SHAPE : STANDING_SHAPE;
    }
}
