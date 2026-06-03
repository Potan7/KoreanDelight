package com.potan.koreandelight.recipe;

import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class ModRecipes {
    public static final RegistrationProvider<RecipeType<?>> RECIPE_TYPES =
            Services.PLATFORM.getProvider(Registries.RECIPE_TYPE);
    public static final RegistrationProvider<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            Services.PLATFORM.getProvider(Registries.RECIPE_SERIALIZER);

    // 발효 레시피 타입 등록
    public static final Supplier<RecipeType<FermentationRecipe>> FERMENTATION_RECIPE_TYPE =
            RECIPE_TYPES.register("fermentation", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "koreandelight:fermentation";
                }
            });

    // 발효 레시피 시리얼라이저 등록
    public static final Supplier<RecipeSerializer<FermentationRecipe>> FERMENTATION_SERIALIZER =
            RECIPE_SERIALIZERS.register("fermentation", FermentationRecipeSerializer::new);

    public static void init() {
        // 클래스 로딩을 위해 호출됩니다.
    }
}
