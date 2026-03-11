package com.potan.koreandelight.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

/**
 * 김장 레시피 JSON 파서
 */
public class KimjangRecipeSerializer implements RecipeSerializer<KimjangRecipe> {

    @Override
    public KimjangRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        // 채소 재료
        Ingredient vegetable = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "vegetable"));

        // 양념 재료들
        JsonArray seasoningArray = GsonHelper.getAsJsonArray(json, "seasonings");
        Ingredient[] seasonings = new Ingredient[seasoningArray.size()];
        for (int i = 0; i < seasoningArray.size(); i++) {
            seasonings[i] = Ingredient.fromJson(seasoningArray.get(i).getAsJsonObject());
        }

        // 필요한 양념 개수 (기본값: 1)
        int seasoningCount = GsonHelper.getAsInt(json, "seasoning_count", 1);

        // 결과 아이템
        ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

        return new KimjangRecipe(recipeId, vegetable, seasonings, output, seasoningCount);
    }

    @Override
    public @Nullable KimjangRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        Ingredient vegetable = Ingredient.fromNetwork(buffer);

        // 양념 개수 읽기
        int seasoningCount = buffer.readInt();
        Ingredient[] seasonings = new Ingredient[seasoningCount];
        for (int i = 0; i < seasoningCount; i++) {
            seasonings[i] = Ingredient.fromNetwork(buffer);
        }

        // 필요한 양념 개수
        int requiredCount = buffer.readInt();

        ItemStack output = buffer.readItem();

        return new KimjangRecipe(recipeId, vegetable, seasonings, output, requiredCount);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, KimjangRecipe recipe) {
        recipe.vegetable.toNetwork(buffer);

        buffer.writeInt(recipe.seasonings.length);
        for (Ingredient seasoning : recipe.seasonings) {
            seasoning.toNetwork(buffer);
        }

        buffer.writeInt(recipe.seasoning_count);
        buffer.writeItem(recipe.output);
    }
}

