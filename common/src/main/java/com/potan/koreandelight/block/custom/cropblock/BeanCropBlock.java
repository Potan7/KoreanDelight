package com.potan.koreandelight.block.custom.cropblock;

import com.potan.koreandelight.item.ModFoodItems;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

public class BeanCropBlock extends CropBlock {
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;

    public BeanCropBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return ModFoodItems.BEAN.get();
    }

    @Override
    public @NotNull IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> pBuilder) {
        pBuilder.add(AGE);
    }
}
