package com.potan.koreandelight.recipe.jei;

import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.recipe.FermentationRecipe;
import com.potan.koreandelight.recipe.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    // 고유 ID
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("koreandelight", "jei_plugin");
    }

    // 1. 카테고리 등록
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FermentationCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    // 2. 레시피 데이터 등록
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // RecipeType을 이용해 로드된 모든 발효 레시피를 가져옴
        List<FermentationRecipe> recipes = recipeManager.getAllRecipesFor(ModRecipes.FERMENTATION_RECIPE_TYPE.get());

        registration.addRecipes(FermentationCategory.TYPE, recipes);
    }

    // 3. 카탈리스트 등록 (어떤 블록을 눌렀을 때 이 카테고리가 뜰지)
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // 옹기 블록을 누르면 발효 레시피를 보여줌
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ONGGI_BLOCK.get()), FermentationCategory.TYPE);
    }
}
