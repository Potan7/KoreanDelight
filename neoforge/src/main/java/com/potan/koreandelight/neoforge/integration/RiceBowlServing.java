package com.potan.koreandelight.neoforge.integration;

import net.minecraft.world.item.ItemStack;

public final class RiceBowlServing {
    private RiceBowlServing() {
    }

    public static ItemStack fillStoredBowls(ItemStack meal, ItemStack bowls, ItemStack output, ItemStack filledBowl) {
        if (!output.isEmpty() && !ItemStack.isSameItemSameComponents(output, filledBowl)) {
            return ItemStack.EMPTY;
        }
        int count = Math.min(Math.min(meal.getCount(), bowls.getCount()), filledBowl.getMaxStackSize() - output.getCount());
        if (count <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack result = filledBowl.copyWithCount(output.getCount() + count);
        meal.shrink(count);
        bowls.shrink(count);
        return result;
    }
}
