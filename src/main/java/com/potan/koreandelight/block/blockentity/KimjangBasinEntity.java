package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

public class KimjangBasinEntity extends SyncedBlockEntity {

    public KimjangBasinEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.KIMJANG_BASIN_ENTITY.get(), pos, state);
    }
}
