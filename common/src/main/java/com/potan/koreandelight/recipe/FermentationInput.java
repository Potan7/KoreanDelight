package com.potan.koreandelight.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

/**
 * 옹기 발효 레시피의 입력 데이터를 담는 클래스입니다. (1.21.1 대응)
 */
public record FermentationInput(List<ItemStack> items) implements RecipeInput {

    public FermentationInput(ItemStack single) {
        this(List.of(single));
    }

    @Override
    public ItemStack getItem(int slot) {
        return (slot >= 0 && slot < items.size()) ? items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return items.size();
    }
}

