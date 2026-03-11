package com.potan.koreandelight.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 김장 레시피 클래스
 * 주재료(채소) + 양념(들) = 김치 아이템으로 변환하는 레시피
 */
public class KimjangRecipe implements Recipe<Container> {

    final ResourceLocation id;
    final Ingredient vegetable; // 채소 (배추, 무 등)
    final Ingredient[] seasonings; // 양념들 (고춧가루, 소금 등)
    final ItemStack output; // 결과 아이템
    final int seasoning_count; // 필요한 양념 개수

    public KimjangRecipe(ResourceLocation id, Ingredient vegetable, Ingredient[] seasonings, ItemStack output, int seasoning_count) {
        this.id = id;
        this.vegetable = vegetable;
        this.seasonings = seasonings;
        this.output = output;
        this.seasoning_count = seasoning_count;
    }

    // 채소가 레시피와 일치하는지 확인
    public boolean matchesVegetable(ItemStack stack) {
        return vegetable.test(stack);
    }

    // 양념이 레시피 양념 중 하나와 일치하는지 확인
    public boolean matchesSeasoning(ItemStack stack) {
        for (Ingredient seasoning : seasonings) {
            if (seasoning.test(stack)) {
                return true;
            }
        }
        return false;
    }

    // 필요한 양념 개수 반환
    public int getRequiredSeasoningCount() {
        return seasoning_count;
    }

    // Container는 사용하지 않으므로 false 반환
    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.KIMJANG_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.KIMJANG_RECIPE_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(vegetable);
        for (Ingredient seasoning : seasonings) {
            list.add(seasoning);
        }
        return list;
    }
}

