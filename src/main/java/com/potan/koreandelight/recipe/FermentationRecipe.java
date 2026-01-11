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

public class FermentationRecipe implements Recipe<Container> {

    final ResourceLocation id;
    final ItemStack output;
    final Ingredient input;
    final int fermentation_time;

    public FermentationRecipe(ResourceLocation id, ItemStack result, Ingredient input, int processTime) {
        this.id = id;
        this.output = result;
        this.input = input;
        this.fermentation_time = processTime;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return input.test(container.getItem(0));
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
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return id;
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
        return fermentation_time;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }
}
