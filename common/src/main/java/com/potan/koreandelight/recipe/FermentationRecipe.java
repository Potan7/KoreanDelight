package com.potan.koreandelight.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FermentationRecipe implements Recipe<FermentationInput> {
    private final ItemStack output;
    private final NonNullList<Ingredient> ingredients;
    private final Optional<FluidStackData> fluid;
    private final Optional<FluidStackData> resultFluid;
    private final int fermentationTime;

    public FermentationRecipe(ItemStack result, NonNullList<Ingredient> ingredients, Optional<FluidStackData> fluid, Optional<FluidStackData> resultFluid, int processTime) {
        this.output = result;
        this.ingredients = ingredients;
        this.fluid = fluid.filter(stack -> !stack.isEmpty());
        this.resultFluid = resultFluid.filter(stack -> !stack.isEmpty());
        this.fermentationTime = processTime;
    }

    public FermentationRecipe(ItemStack result, Ingredient input, Optional<FluidStackData> fluid, Optional<FluidStackData> resultFluid, int processTime) {
        this(result, createSingleList(input), fluid, resultFluid, processTime);
    }

    private static NonNullList<Ingredient> createSingleList(Ingredient input) {
        NonNullList<Ingredient> list = NonNullList.create();
        if (input != null && !input.isEmpty()) {
            list.add(input);
        }
        return list;
    }

    @Override
    public boolean matches(FermentationInput input, Level level) {
        List<ItemStack> nonEmptyInputs = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                nonEmptyInputs.add(stack);
            }
        }

        List<Ingredient> nonEmptyIngredients = new ArrayList<>();
        for (Ingredient ing : this.ingredients) {
            if (!ing.isEmpty()) {
                nonEmptyIngredients.add(ing);
            }
        }

        if (nonEmptyInputs.size() != nonEmptyIngredients.size()) {
            return false;
        }

        return matchIngredients(nonEmptyInputs, nonEmptyIngredients, 0, new boolean[nonEmptyInputs.size()]);
    }

    private boolean matchIngredients(List<ItemStack> inputs, List<Ingredient> ingredients, int ingIndex, boolean[] used) {
        if (ingIndex >= ingredients.size()) {
            return true;
        }
        Ingredient ing = ingredients.get(ingIndex);
        for (int i = 0; i < inputs.size(); i++) {
            if (!used[i] && ing.test(inputs.get(i))) {
                used[i] = true;
                if (matchIngredients(inputs, ingredients, ingIndex + 1, used)) {
                    return true;
                }
                used[i] = false;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(FermentationInput input, HolderLookup.Provider access) {
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

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FERMENTATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FERMENTATION_RECIPE_TYPE.get();
    }

    public int getFermentation_time() {
        return fermentationTime;
    }

    public int getFermentationTime() {
        return fermentationTime;
    }

    public Ingredient getInput() {
        return ingredients.isEmpty() ? Ingredient.EMPTY : ingredients.get(0);
    }

    public ItemStack getOutput() {
        return output;
    }

    public Optional<FluidStackData> getFluid() {
        return fluid;
    }

    public Optional<FluidStackData> getResultFluid() {
        return resultFluid;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }
}

