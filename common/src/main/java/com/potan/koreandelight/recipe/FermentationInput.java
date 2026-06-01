package com.potan.koreandelight.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * 옹기 발효 레시피의 입력 데이터를 담는 클래스입니다. (1.21.1 대응)
 */
public record FermentationInput(ItemStack inputStack) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? inputStack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}
