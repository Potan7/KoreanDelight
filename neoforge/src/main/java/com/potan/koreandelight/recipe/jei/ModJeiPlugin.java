package com.potan.koreandelight.recipe.jei;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.block.blockentity.KimjangBasinBlockEntity;
import com.potan.koreandelight.block.blockentity.MortarBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import com.potan.koreandelight.item.ModFoodItems;
import com.potan.koreandelight.item.ModItems;
import com.potan.koreandelight.recipe.FermentationRecipe;
import com.potan.koreandelight.recipe.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.integration.jei.FDRecipeTypes;

import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Koreandelight.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FermentationCategory(registration.getJeiHelpers().getGuiHelper()),
                new MortarCategory(registration.getJeiHelpers().getGuiHelper()),
                new KimjangCategory(registration.getJeiHelpers().getGuiHelper()));
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

        // Derive displayed ratios from the same processing logic used by the mortar.
        List<MortarCategory.Recipe> mortarRecipes = BuiltInRegistries.ITEM.stream()
                .map(item -> {
                    ItemStack input = new ItemStack(item);
                    ItemStack output = MortarBlockEntity.result(input);
                    if (output.isEmpty()) {
                        input = new ItemStack(item, 2);
                        output = MortarBlockEntity.result(input);
                    }
                    return new MortarCategory.Recipe(input, output);
                })
                .filter(recipe -> !recipe.output().isEmpty())
                .toList();
        registration.addRecipes(MortarCategory.TYPE, mortarRecipes);
        int batch = KimjangBasinBlockEntity.BATCH_SIZE;
        int seasoning = KimjangBasinBlockEntity.SEASONING_COUNT;
        registration.addRecipes(KimjangCategory.TYPE, List.of(
                new KimjangCategory.Recipe(
                        Component.translatable("jei.koreandelight.kimjang.mix", KimjangBasinBlockEntity.MIXES),
                        List.of(new ItemStack(ModFoodItems.KIMCHI_CABBAGE.get(), batch),
                                new ItemStack(ModItems.RED_PEPPER_POWDER.get(), seasoning),
                                new ItemStack(ModItems.MINCED_GARLIC.get(), seasoning)),
                        List.of(new ItemStack(ModFoodItems.KIMCHI.get(), batch)))));

        // Serving containers do not select the cooking recipe; show this variant in JEI only.
        var level = Minecraft.getInstance().level;
        List<RecipeHolder<CookingPotRecipe>> riceBowlRecipes = level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.COOKING.get()).stream()
                .filter(holder -> holder.value().getResultItem(level.registryAccess())
                        .is(vectorwing.farmersdelight.common.registry.ModItems.COOKED_RICE.get()))
                .map(holder -> {
                    CookingPotRecipe source = holder.value();
                    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Koreandelight.MODID,
                            "jei/" + holder.id().getNamespace() + "/" + holder.id().getPath() + "_in_rice_bowl");
                    return new RecipeHolder<>(id, new CookingPotRecipe(source.getGroup(), source.getRecipeBookTab(),
                            source.getIngredients(), new ItemStack(ModFoodItems.COOKED_RICE_BOWL.get(),
                            source.getResultItem(level.registryAccess()).getCount()), new ItemStack(ModItems.RICE_BOWL.get()),
                            source.getExperience(), source.getCookTime()));
                })
                .toList();
        registration.addRecipes(FDRecipeTypes.COOKING, riceBowlRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.ONGGI_BLOCK.get(), FermentationCategory.TYPE);
        registration.addRecipeCatalyst(ModBlocks.MORTAR.get(), MortarCategory.TYPE);
        registration.addRecipeCatalyst(ModBlocks.KIMJANG_BASIN.get(), KimjangCategory.TYPE);
    }
}
