package com.potan.koreandelight.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class FermentationRecipe implements Recipe<FermentationInput> {
    private final ItemStack output;
    private final Ingredient input;
    private final Optional<FluidStackData> fluid;
    private final Optional<FluidStackData> resultFluid;
    private final int fermentationTime;

    public FermentationRecipe(ItemStack result, Ingredient input, Optional<FluidStackData> fluid, Optional<FluidStackData> resultFluid, int processTime) {
        this.output = result;
        this.input = input;
        this.fluid = fluid.filter(stack -> !stack.isEmpty());
        this.resultFluid = resultFluid.filter(stack -> !stack.isEmpty());
        this.fermentationTime = processTime;
    }

    @Override
    public boolean matches(FermentationInput input, Level level) {
        ItemStack stack = input.getItem(0);
        return !stack.isEmpty() && this.input.test(stack);
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
        return input;
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
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }
}
