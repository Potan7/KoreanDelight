package com.potan.koreandelight.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    // 레시피 타입과 시리얼라이저를 등록하기 위한 DeferredRegister 생성
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, "koreandelight");
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, "koreandelight");

    // 발효 레시피 타입 등록
    public static final RegistryObject<RecipeType<FermentationRecipe>> FERMENTATION_RECIPE_TYPE =
            RECIPE_TYPES.register("fermentation", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "koreandelight:fermentation";
                }
            });

    // 발효 레시피 시리얼라이저 등록
    public static final RegistryObject<RecipeSerializer<FermentationRecipe>> FERMENTATION_SERIALIZER =
            RECIPE_SERIALIZERS.register("fermentation", FermentationRecipeSerializer::new);

    // 김장 레시피 타입 등록
    public static final RegistryObject<RecipeType<KimjangRecipe>> KIMJANG_RECIPE_TYPE =
            RECIPE_TYPES.register("kimjang", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "koreandelight:kimjang";
                }
            });

    // 김장 레시피 시리얼라이저 등록
    public static final RegistryObject<RecipeSerializer<KimjangRecipe>> KIMJANG_SERIALIZER =
            RECIPE_SERIALIZERS.register("kimjang", KimjangRecipeSerializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }

}
