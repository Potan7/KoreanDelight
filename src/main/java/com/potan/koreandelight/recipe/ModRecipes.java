package com.potan.koreandelight.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, "koreandelight");
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, "koreandelight");

    public static final RegistryObject<RecipeType<FermentationRecipe>> FERMENTATION_RECIPE_TYPE =
            RECIPE_TYPES.register("fermentation", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "koreandelight:fermentation";
                }
            });

    public static final RegistryObject<RecipeSerializer<FermentationRecipe>> FERMENTATION_SERIALIZER =
            RECIPE_SERIALIZERS.register("fermentation", FermentationRecipeSerializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }

}
