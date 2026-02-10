package com.potan.koreandelight.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class FermentationRecipeSerializer implements RecipeSerializer<FermentationRecipe> {

    // JSON 파일로부터 레시피 읽기
    @Override
    public FermentationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
        
        // 입력 액체 (선택 사항)
        FluidStack fluid = FluidStack.EMPTY;
        if (json.has("fluid")) {
            fluid = readFluid(GsonHelper.getAsJsonObject(json, "fluid"));
        }
        
        // 결과 액체 (선택 사항)
        FluidStack fluidResult = FluidStack.EMPTY;
        if (json.has("result_fluid")) {
            fluidResult = readFluid(GsonHelper.getAsJsonObject(json, "result_fluid"));
        }
        
        // 발효 시간 (기본값 200틱)
        int fermentation_time = GsonHelper.getAsInt(json, "fermentation_time", 200);

        return new FermentationRecipe(recipeId, output, input, fluid, fluidResult, fermentation_time);
    }

    // JSON 객체에서 FluidStack 읽기 헬퍼 메서드
    private FluidStack readFluid(JsonObject json) {
        String fluidId = GsonHelper.getAsString(json, "fluid");
        int amount = GsonHelper.getAsInt(json, "amount", 1000);
        return new FluidStack(ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(fluidId)), amount);
    }

    // 네트워크 패킷 읽기 (서버 -> 클라이언트 동기화)
    @Override
    public @Nullable FermentationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        Ingredient input = Ingredient.fromNetwork(buffer);
        ItemStack output = buffer.readItem();
        FluidStack fluid = buffer.readFluidStack();
        FluidStack fluidResult = buffer.readFluidStack();
        int fermentation_time = buffer.readInt();
        return new FermentationRecipe(recipeId, output, input, fluid, fluidResult, fermentation_time);
    }

    // 네트워크 패킷 쓰기 (서버 -> 클라이언트 동기화)
    @Override
    public void toNetwork(FriendlyByteBuf buffer, FermentationRecipe recipe) {
        recipe.input.toNetwork(buffer);
        buffer.writeItem(recipe.output);
        buffer.writeFluidStack(recipe.fluid);
        buffer.writeFluidStack(recipe.fluidResult);
        buffer.writeInt(recipe.fermentation_time);
    }
}
