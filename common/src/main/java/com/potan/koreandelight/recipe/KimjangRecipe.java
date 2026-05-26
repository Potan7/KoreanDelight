package com.potan.koreandelight.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.List;

public class KimjangRecipe implements Recipe<KimjangInput> {
    private final Ingredient vegetable;
    private final List<Ingredient> seasonings;
    private final ItemStack output;
    private final int seasoningCount;

    public KimjangRecipe(Ingredient vegetable, List<Ingredient> seasonings, ItemStack output, int seasoningCount) {
        this.vegetable = vegetable;
        this.seasonings = List.copyOf(seasonings);
        this.output = output;
        this.seasoningCount = seasoningCount;
    }

    public boolean matchesVegetable(ItemStack stack) {
        return vegetable.test(stack);
    }

    public boolean matchesSeasoning(ItemStack stack) {
        for (Ingredient seasoning : seasonings) {
            if (seasoning.test(stack)) return true;
        }
        return false;
    }

    public int getRequiredSeasoningCount() {
        return seasoningCount;
    }

    @Override
    public boolean matches(KimjangInput input, Level level) {
        if (input.vegetable().isEmpty() || !matchesVegetable(input.vegetable())) {
            return false;
        }

        if (input.seasonings().size() != seasoningCount) {
            return false;
        }

        for (ItemStack seasoningStack : input.seasonings()) {
            if (!matchesSeasoning(seasoningStack)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(KimjangInput input, HolderLookup.Provider access) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider access) {
        return output;
    }

    public Ingredient getVegetable() {
        return vegetable;
    }

    public List<Ingredient> getSeasonings() {
        return seasonings;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getSeasoningCount() {
        return seasoningCount;
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
        list.addAll(seasonings);
        return list;
    }
}
