package com.potan.koreandelight.recipe;

import com.mojang.serialization.MapCodec;
import com.potan.koreandelight.item.ModFoodItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class KimchiBowlRecipe extends ShapelessRecipe {
    public KimchiBowlRecipe(ShapelessRecipe recipe) {
        super(recipe.getGroup(), recipe.category(), recipe.getResultItem(null), recipe.getIngredients());
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = super.assemble(input, registries);
        for (ItemStack stack : input.items()) {
            if (stack.is(ModFoodItems.KIMCHI.get())) {
                FoodProperties source = stack.get(DataComponents.FOOD);
                FoodProperties bowl = result.get(DataComponents.FOOD);
                if (source != null && bowl != null) {
                    result.set(DataComponents.FOOD, new FoodProperties(bowl.nutrition(), bowl.saturation(),
                            bowl.canAlwaysEat(), bowl.eatSeconds(), bowl.usingConvertsTo(), source.effects()));
                }
                break;
            }
        }
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.KIMCHI_BOWL_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<KimchiBowlRecipe> {
        private static final ShapelessRecipe.Serializer BASE = new ShapelessRecipe.Serializer();
        private static final MapCodec<KimchiBowlRecipe> CODEC = BASE.codec().xmap(KimchiBowlRecipe::new, recipe -> recipe);
        private static final StreamCodec<RegistryFriendlyByteBuf, KimchiBowlRecipe> STREAM =
                BASE.streamCodec().map(KimchiBowlRecipe::new, recipe -> recipe);

        @Override public MapCodec<KimchiBowlRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, KimchiBowlRecipe> streamCodec() { return STREAM; }
    }
}
