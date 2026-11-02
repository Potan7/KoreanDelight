package com.potan.koreandelight.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class FermentationRecipe implements Recipe<FermentationInput> {
    private final ItemStack output;
    private final NonNullList<Ingredient> ingredients;
    private final Optional<FluidStackData> fluid;
    private final Optional<FluidStackData> resultFluid;
    private final int fermentationTime;
    private final FermentationEnvironment environment;

    public FermentationRecipe(ItemStack result, NonNullList<Ingredient> ingredients, Optional<FluidStackData> fluid, Optional<FluidStackData> resultFluid, int processTime) {
        this(result, ingredients, fluid, resultFluid, processTime, FermentationEnvironment.ANY);
    }

    public FermentationRecipe(ItemStack result, NonNullList<Ingredient> ingredients, Optional<FluidStackData> fluid, Optional<FluidStackData> resultFluid, int processTime, FermentationEnvironment environment) {
        this.output = result;
        this.ingredients = ingredients;
        this.fluid = fluid.filter(stack -> !stack.isEmpty());
        this.resultFluid = resultFluid.filter(stack -> !stack.isEmpty());
        this.fermentationTime = processTime;
        this.environment = environment;
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
        return getBatchSize(input) > 0;
    }

    public int getBatchSize(FermentationInput input) {
        int ingredientCount = (int) ingredients.stream().filter(ingredient -> !ingredient.isEmpty()).count();
        if (ingredientCount == 0) {
            return 0;
        }
        StackedContents contents = new StackedContents();
        int total = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            total += stack.getCount();
            contents.accountStack(stack);
        }
        if (total == 0 || total % ingredientCount != 0) {
            return 0;
        }
        int batchSize = total / ingredientCount;
        return contents.canCraft(this, null, batchSize) ? batchSize : 0;
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

    public FermentationEnvironment getEnvironment() {
        return environment;
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

