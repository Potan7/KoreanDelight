package com.potan.koreandelight.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class FluidBottleItem extends Item {
    private final Supplier<? extends Fluid> fluid;

    public FluidBottleItem(Supplier<? extends Fluid> fluid) {
        super(new Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16));
        this.fluid = fluid;
    }

    public Fluid getFluid() {
        return fluid.get();
    }
}
