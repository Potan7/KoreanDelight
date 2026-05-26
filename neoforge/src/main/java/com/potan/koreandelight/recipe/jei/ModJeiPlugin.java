package com.potan.koreandelight.recipe.jei;

import com.potan.koreandelight.Koreandelight;
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

import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Koreandelight.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FermentationCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) {
            return;
        }

        List<FermentationRecipe> recipes = Minecraft.getInstance().level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.FERMENTATION_RECIPE_TYPE.get())
                .stream()
                .map(holder -> holder.value())
                .toList();

        registration.addRecipes(FermentationCategory.TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.ONGGI_BLOCK.get(), FermentationCategory.TYPE);
    }
}
