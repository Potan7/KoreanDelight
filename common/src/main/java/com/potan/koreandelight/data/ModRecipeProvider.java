package com.potan.koreandelight.data;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.custom.RiceBowlBlock;
import com.potan.koreandelight.item.ModFoodItems;
import com.potan.koreandelight.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
        seedFromCrop(output, ModFoodItems.KIMCHI_CABBAGE.get(), ModItems.KIMCHI_CABBAGE_SEEDS.get(), "kimchi_cabbage");
        seedFromCrop(output, ModFoodItems.RED_PEPPER.get(), ModItems.RED_PEPPER_SEEDS.get(), "red_pepper");
        seedFromCrop(output, ModFoodItems.GREEN_ONION.get(), ModItems.GREEN_ONION_SEEDS.get(), "green_onion");
        seedFromCrop(output, ModFoodItems.LETTUCE.get(), ModItems.LETTUCE_SEEDS.get(), "lettuce");
        seedFromCrop(output, ModFoodItems.PERILLA_LEAF.get(), ModItems.PERILLA_SEEDS.get(), "perilla");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.KIMCHI_BOWL.get())
                .requires(ModItems.RICE_BOWL.get()).requires(ModFoodItems.KIMCHI.get())
                .unlockedBy("has_kimchi", has(ModFoodItems.KIMCHI.get()))
                .save(output, modRecipe("kimchi_bowl"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.LETTUCE_BOWL.get())
                .requires(Items.BOWL).requires(ModFoodItems.LETTUCE.get(), RiceBowlBlock.DISH_SERVINGS)
                .unlockedBy("has_lettuce", has(ModFoodItems.LETTUCE.get()))
                .save(output, modRecipe("lettuce_bowl"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.LETTUCE.get(), RiceBowlBlock.DISH_SERVINGS)
                .requires(ModFoodItems.LETTUCE_BOWL.get())
                .unlockedBy("has_lettuce_bowl", has(ModFoodItems.LETTUCE_BOWL.get()))
                .save(output, modRecipe("lettuce_from_bowl"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.PERILLA_LEAF_BOWL.get())
                .requires(Items.BOWL).requires(ModFoodItems.PERILLA_LEAF.get(), RiceBowlBlock.DISH_SERVINGS)
                .unlockedBy("has_perilla_leaf", has(ModFoodItems.PERILLA_LEAF.get()))
                .save(output, modRecipe("perilla_leaf_bowl"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.PERILLA_LEAF.get(), RiceBowlBlock.DISH_SERVINGS)
                .requires(ModFoodItems.PERILLA_LEAF_BOWL.get())
                .unlockedBy("has_perilla_leaf_bowl", has(ModFoodItems.PERILLA_LEAF_BOWL.get()))
                .save(output, modRecipe("perilla_leaf_from_bowl"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.KIMCHI.get())
                .requires(ModFoodItems.KIMCHI_BOWL.get())
                .unlockedBy("has_kimchi_bowl", has(ModFoodItems.KIMCHI_BOWL.get()))
                .save(output, modRecipe("kimchi_from_bowl"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.GARLIC_BOWL.get())
                .requires(ModItems.RICE_BOWL.get()).requires(ModItems.GARLIC.get())
                .unlockedBy("has_garlic", has(ModItems.GARLIC.get()))
                .save(output, modRecipe("garlic_bowl"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.GARLIC.get())
                .requires(ModFoodItems.GARLIC_BOWL.get())
                .unlockedBy("has_garlic_bowl", has(ModFoodItems.GARLIC_BOWL.get()))
                .save(output, modRecipe("garlic_from_bowl"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.SSAMJANG_BOWL.get())
                .requires(ModItems.RICE_BOWL.get()).requires(ModFoodItems.SSAMJANG.get())
                .unlockedBy("has_ssamjang", has(ModFoodItems.SSAMJANG.get()))
                .save(output, modRecipe("ssamjang_bowl"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.SSAMJANG.get())
                .requires(ModFoodItems.SSAMJANG_BOWL.get())
                .unlockedBy("has_ssamjang_bowl", has(ModFoodItems.SSAMJANG_BOWL.get()))
                .save(output, modRecipe("ssamjang_from_bowl"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.SSAMJANG.get())
                .requires(ModFoodItems.DOENJANG.get()).requires(ModItems.GOCHUJANG.get())
                .requires(ModItems.MINCED_GARLIC.get())
                .unlockedBy("has_doenjang", has(ModFoodItems.DOENJANG.get()))
                .save(output, modRecipe("ssamjang"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.DOENJANG.get(), 4)
                .requires(ModFoodItems.DOENJANG_BLOCK_ITEM.get())
                .unlockedBy("has_doenjang_block", has(ModFoodItems.DOENJANG_BLOCK_ITEM.get()))
                .save(output, modRecipe("doenjang_from_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MEJU_BLOCK_ITEM.get())
                .pattern("BB").pattern("BB").define('B', ModFoodItems.BEAN.get())
                .unlockedBy("has_bean", has(ModFoodItems.BEAN.get()))
                .save(output, modRecipe("meju_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ONGGI_BLOCK_ITEM.get())
                .pattern("B B").pattern("B B").pattern("BBB").define('B', Items.BRICK)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(output, modRecipe("onggi_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RICE_BOWL.get())
                .pattern("I I")
                .pattern(" I ")
                .define('I', Items.IRON_NUGGET)
                .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
                .save(output, modRecipe("rice_bowl"));

        Item cookedRice = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("farmersdelight", "cooked_rice"));
        // Keep the onggi chain obtainable before dedicated grinding and grain crops arrive.
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SALT.get(), 4)
                .requires(Items.WATER_BUCKET)
                .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
                .save(output, modRecipe("salt"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.MEJU_POWDER.get(), 4)
                .requires(ModItems.FERMENTED_MEJU.get())
                .unlockedBy("has_fermented_meju", has(ModItems.FERMENTED_MEJU.get()))
                .save(output, modRecipe("meju_powder"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.GLUTINOUS_RICE_PASTE.get())
                .requires(cookedRice).requires(Items.WATER_BUCKET)
                .unlockedBy("has_cooked_rice", has(cookedRice))
                .save(output, modRecipe("glutinous_rice_paste"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.FRESH_KIMCHI.get())
                .requires(ModFoodItems.KIMCHI_CABBAGE.get()).requires(ModItems.RED_PEPPER_POWDER.get())
                .requires(ModItems.MINCED_GARLIC.get()).requires(ModItems.FISH_SAUCE.get())
                .unlockedBy("has_fish_sauce", has(ModItems.FISH_SAUCE.get()))
                .save(output, modRecipe("fresh_kimchi"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModFoodItems.COOKED_RICE_BOWL.get())
                .requires(ModItems.RICE_BOWL.get())
                .requires(cookedRice)
                .unlockedBy("has_rice_bowl", has(ModItems.RICE_BOWL.get()))
                .save(output, modRecipe("cooked_rice_bowl"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, cookedRice)
                .requires(ModFoodItems.COOKED_RICE_BOWL.get()).requires(Items.BOWL)
                .unlockedBy("has_cooked_rice_bowl", has(ModFoodItems.COOKED_RICE_BOWL.get()))
                .save(output, modRecipe("cooked_rice_from_rice_bowl"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FISH_CAKE.get())
                .requires(ModItems.MINCED_FISH.get())
                .requires(WHEAT_DOUGH)
                .requires(ModItems.MINCED_GARLIC.get())
                .unlockedBy("has_minced_fish", has(ModItems.MINCED_FISH.get()))
                .save(output, modRecipe("raw_fish_cake"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_KIMCHI_JEON.get())
                .requires(Items.WHEAT)
                .requires(Items.EGG)
                .requires(ModFoodItems.KIMCHI.get())
                .requires(ModItems.SOY_SAUCE_BOTTLE.get())
                .unlockedBy("has_kimchi", has(ModFoodItems.KIMCHI.get()))
                .save(output, modRecipe("raw_kimchi_jeon"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_KIMCHI_JEON.get(), 2)
                .requires(WHEAT_DOUGH)
                .requires(Items.EGG)
                .requires(ModFoodItems.KIMCHI.get())
                .requires(ModItems.SOY_SAUCE_BOTTLE.get())
                .unlockedBy("has_kimchi", has(ModFoodItems.KIMCHI.get()))
                .save(output, modRecipe("raw_kimchi_jeon_from_wheat_dough"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_PAJEON.get())
                .requires(Items.WHEAT)
                .requires(Items.EGG)
                .requires(ModFoodItems.GREEN_ONION.get())
                .requires(ModItems.MINCED_FISH.get())
                .requires(ModItems.SOY_SAUCE_BOTTLE.get())
                .unlockedBy("has_minced_fish", has(ModItems.MINCED_FISH.get()))
                .save(output, modRecipe("raw_pajeon"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_PAJEON.get(), 2)
                .requires(WHEAT_DOUGH)
                .requires(Items.EGG)
                .requires(ModFoodItems.GREEN_ONION.get())
                .requires(ModItems.MINCED_FISH.get())
                .requires(ModItems.SOY_SAUCE_BOTTLE.get())
                .unlockedBy("has_minced_fish", has(ModItems.MINCED_FISH.get()))
                .save(output, modRecipe("raw_pajeon_from_wheat_dough"));

        cookFood(output, ModItems.RAW_FISH_CAKE.get(), ModFoodItems.FISH_CAKE.get(), "raw_fish_cake", "fish_cake");
        cookFood(output, ModItems.RAW_KIMCHI_JEON.get(), ModFoodItems.KIMCHI_JEON.get(), "raw_kimchi_jeon", "kimchi_jeon");
        cookFood(output, ModItems.RAW_PAJEON.get(), ModFoodItems.PAJEON.get(), "raw_pajeon", "pajeon");
    }

    private static void cookFood(RecipeOutput output, Item raw, Item cooked, String rawName, String cookedName) {
        Ingredient ingredient = Ingredient.of(raw);
        SimpleCookingRecipeBuilder.smelting(ingredient, RecipeCategory.FOOD, cooked, 0.35F, 200)
                .unlockedBy("has_" + rawName, has(raw))
                .save(output, modRecipe(cookedName + "_from_smelting"));
        SimpleCookingRecipeBuilder.smoking(ingredient, RecipeCategory.FOOD, cooked, 0.35F, 100)
                .unlockedBy("has_" + rawName, has(raw))
                .save(output, modRecipe(cookedName + "_from_smoking"));
        SimpleCookingRecipeBuilder.campfireCooking(ingredient, RecipeCategory.FOOD, cooked, 0.35F, 600)
                .unlockedBy("has_" + rawName, has(raw))
                .save(output, modRecipe(cookedName + "_from_campfire_cooking"));
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
