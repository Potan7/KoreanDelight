package com.potan.koreandelight.data;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.item.ModFoodItems;
import com.potan.koreandelight.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

/**
 * Korean Delight의 기본 제작대 레시피를 생성하는 데이터 생성기입니다.
 * 수동 JSON 대신 Builder API를 사용해 1.21.1 레시피 포맷 변화를 따라갑니다.
 */
public class ModRecipeProvider extends RecipeProvider {
    private static final TagKey<Item> WHEAT_DOUGH = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("c", "foods/dough/wheat")
    );

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.DOENJANG.get(), 16)
                .requires(ModBlocks.DOENJANG_BLOCK.get())
                .unlockedBy("has_doenjang_block", has(ModBlocks.DOENJANG_BLOCK.get()))
                .save(output, modRecipe("doenjang"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.KIMCHI.get())
                .requires(ModFoodItems.KIMCHI_CABBAGE.get())
                .requires(ModItems.RED_PEPPER_POWDER.get(), 2)
                .unlockedBy("has_kimchi_cabbage", has(ModFoodItems.KIMCHI_CABBAGE.get()))
                .unlockedBy("has_red_pepper_powder", has(ModItems.RED_PEPPER_POWDER.get()))
                .save(output, modRecipe("kimchi"));

        seedFromCrop(output, ModFoodItems.KIMCHI_CABBAGE.get(), ModItems.KIMCHI_CABBAGE_SEEDS.get(), "kimchi_cabbage");
        seedFromCrop(output, ModFoodItems.RED_PEPPER.get(), ModItems.RED_PEPPER_SEEDS.get(), "red_pepper");
        seedFromCrop(output, ModFoodItems.GREEN_ONION.get(), ModItems.GREEN_ONION_SEEDS.get(), "green_onion");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FISH_CAKE.get())
                .requires(ModItems.MINCED_FISH.get())
                .requires(WHEAT_DOUGH)
                .requires(ModItems.MINCED_GARLIC.get())
                .unlockedBy("has_minced_fish", has(ModItems.MINCED_FISH.get()))
                .save(output, modRecipe("raw_fish_cake"));

        Ingredient rawFishCake = Ingredient.of(ModItems.RAW_FISH_CAKE.get());
        SimpleCookingRecipeBuilder.smelting(rawFishCake, RecipeCategory.FOOD, ModFoodItems.FISH_CAKE.get(), 0.35F, 200)
                .unlockedBy("has_raw_fish_cake", has(ModItems.RAW_FISH_CAKE.get()))
                .save(output, modRecipe("fish_cake_from_smelting"));
        SimpleCookingRecipeBuilder.smoking(rawFishCake, RecipeCategory.FOOD, ModFoodItems.FISH_CAKE.get(), 0.35F, 100)
                .unlockedBy("has_raw_fish_cake", has(ModItems.RAW_FISH_CAKE.get()))
                .save(output, modRecipe("fish_cake_from_smoking"));
        SimpleCookingRecipeBuilder.campfireCooking(rawFishCake, RecipeCategory.FOOD, ModFoodItems.FISH_CAKE.get(), 0.35F, 600)
                .unlockedBy("has_raw_fish_cake", has(ModItems.RAW_FISH_CAKE.get()))
                .save(output, modRecipe("fish_cake_from_campfire_cooking"));
    }

    private static ResourceLocation modRecipe(String name) {
        return ResourceLocation.fromNamespaceAndPath(Koreandelight.MODID, name);
    }

    private static void seedFromCrop(RecipeOutput output, Item crop, Item seeds, String name) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, seeds)
                .requires(crop)
                .unlockedBy("has_" + name, has(crop))
                .save(output, modRecipe(name + "_seeds"));
    }
}
