package com.potan.koreandelight.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import java.util.List;

/**
 * 김장 대야 레시피의 입력 데이터를 담는 클래스입니다. (1.21.1 대응)
 */
public record KimjangInput(ItemStack vegetable, List<ItemStack> seasonings) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        if (slot == 0) return vegetable;
        if (slot > 0 && slot <= seasonings.size()) return seasonings.get(slot - 1);
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1 + seasonings.size();
    }
}
