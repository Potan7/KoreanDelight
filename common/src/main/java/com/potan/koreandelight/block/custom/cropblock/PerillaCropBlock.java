package com.potan.koreandelight.block.custom.cropblock;

import com.potan.koreandelight.item.ModItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;

public class PerillaCropBlock extends CropBlock {
    public PerillaCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.PERILLA_SEEDS.get();
    }
}
