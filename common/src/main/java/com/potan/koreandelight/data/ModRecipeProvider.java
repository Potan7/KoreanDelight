package com.potan.koreandelight.data;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.item.ModFoodItems;
import com.potan.koreandelight.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

/**
 * Korean Delight의 기본 제작대 레시피를 생성하는 데이터 생성기입니다.
 * 수동 JSON 대신 Builder API를 사용해 1.21.1 레시피 포맷 변화를 따라갑니다.
 */
public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.DOENJANG.get(), 16)
                .requires(ModBlocks.DOENJANG_BLOCK.get())
                .unlockedBy("has_doenjang_block", has(ModBlocks.DOENJANG_BLOCK.get()))
                .save(output, modRecipe("doenjang"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.FRESH_KIMCHI.get())
                .requires(ModFoodItems.KIMCHI_CABBAGE.get())
                .requires(ModItems.RED_PEPPER_POWDER.get(), 2)
                .unlockedBy("has_kimchi_cabbage", has(ModFoodItems.KIMCHI_CABBAGE.get()))
                .unlockedBy("has_red_pepper_powder", has(ModItems.RED_PEPPER_POWDER.get()))
                .save(output, modRecipe("fresh_kimchi"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.KIMCHI_CABBAGE_SEEDS.get())
                .requires(ModFoodItems.KIMCHI_CABBAGE.get())
                .unlockedBy("has_kimchi_cabbage", has(ModFoodItems.KIMCHI_CABBAGE.get()))
                .save(output, modRecipe("kimchi_cabbage_seeds"));
    }

    private static ResourceLocation modRecipe(String name) {
        return ResourceLocation.fromNamespaceAndPath(Koreandelight.MODID, name);
    }
}
