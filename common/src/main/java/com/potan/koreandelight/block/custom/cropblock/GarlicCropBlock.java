package com.potan.koreandelight.block.custom.cropblock;

import com.potan.koreandelight.item.ModItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import org.jetbrains.annotations.NotNull;

public class GarlicCropBlock extends CropBlock {
    public GarlicCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return ModItems.GARLIC.get();
    }
}
