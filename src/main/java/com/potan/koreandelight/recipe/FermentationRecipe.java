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

import net.minecraftforge.fluids.FluidStack;

public class FermentationRecipe implements Recipe<Container> {

    final ResourceLocation id; // 레시피 ID
    final ItemStack output; // 결과 아이템
    final Ingredient input; // 입력 재료 (아이템)
    final FluidStack fluid; // 입력 액체
    final FluidStack fluidResult; // 결과 액체
    final int fermentation_time; // 발효 시간 (틱 단위)

    public FermentationRecipe(ResourceLocation id, ItemStack result, Ingredient input, FluidStack fluid, FluidStack fluidResult, int processTime) {
        this.id = id;
        this.output = result;
        this.input = input;
        this.fluid = fluid;
        this.fluidResult = fluidResult;
        this.fermentation_time = processTime;
    }

    // 인벤토리의 아이템이 레시피와 일치하는지 확인
    @Override
    public boolean matches(Container container, Level level) {
        return input.test(container.getItem(0));
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public FluidStack getFluidResult() {
        return fluidResult;
    }

    // 결과 아이템 생성
    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        return output.copy();
    }

    // 제작 그리드 크기 확인 (여기서는 중요하지 않음)
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    // 결과 아이템 반환
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
