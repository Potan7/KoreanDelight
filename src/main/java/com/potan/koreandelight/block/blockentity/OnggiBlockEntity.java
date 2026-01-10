package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

public class OnggiBlockEntity extends SyncedBlockEntity {
    private int agingProgress = 0; // 숙성도 데이터 추가

    public OnggiBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get(), pos, state);
    }

    // 데이터를 저장할 때 (서버 -> NBT)
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("aging_progress", this.agingProgress); // 이제 데이터가 생겼습니다!
    }

    // 데이터를 불러올 때 (NBT -> 객체)
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.agingProgress = tag.getInt("aging_progress");
    }

}
