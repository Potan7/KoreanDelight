package com.potan.koreandelight.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

public class FermentationRecipeSerializer implements RecipeSerializer<FermentationRecipe> {

    // JSON 파일 읽기
    @Override
    public FermentationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
        int fermentation_time = GsonHelper.getAsInt(json, "fermentation_time", 200); // 기본값 200틱

        return new FermentationRecipe(recipeId, output, input, fermentation_time);
    }

    // 네트워크 패킷 읽기 (서버 -> 클라이언트)
    @Override
    public @Nullable FermentationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        Ingredient input = Ingredient.fromNetwork(buffer);
        ItemStack output = buffer.readItem();
        int fermentation_time = buffer.readInt();
        return new FermentationRecipe(recipeId, output, input, fermentation_time);
    }

    // 네트워크 패킷 쓰기
    @Override
    public void toNetwork(FriendlyByteBuf buffer, FermentationRecipe recipe) {
        recipe.input.toNetwork(buffer);
        buffer.writeItem(recipe.output);
        buffer.writeInt(recipe.fermentation_time);
    }
}
