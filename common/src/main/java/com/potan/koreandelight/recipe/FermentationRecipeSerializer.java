package com.potan.koreandelight.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FermentationRecipeSerializer implements RecipeSerializer<FermentationRecipe> {
    private static final MapCodec<FermentationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(FermentationRecipe::getOutput),
                    Ingredient.CODEC_NONEMPTY.listOf().optionalFieldOf("ingredients").forGetter(r -> Optional.of(new ArrayList<>(r.getIngredients()))),
                    Ingredient.CODEC_NONEMPTY.optionalFieldOf("ingredient").forGetter(r -> Optional.empty()),
                    FluidStackData.CODEC.optionalFieldOf("fluid").forGetter(FermentationRecipe::getFluid),
                    FluidStackData.CODEC.optionalFieldOf("result_fluid").forGetter(FermentationRecipe::getResultFluid),
                    Codec.INT.optionalFieldOf("fermentation_time", 200)
                            .validate(time -> time > 0
                                    ? DataResult.success(time)
                                    : DataResult.error(() -> "fermentation_time must be positive"))
                            .forGetter(FermentationRecipe::getFermentationTime)
            ).apply(inst, (result, ingredientsOpt, ingredientOpt, fluid, resultFluid, time) -> {
                NonNullList<Ingredient> list = NonNullList.create();
                if (ingredientsOpt.isPresent() && !ingredientsOpt.get().isEmpty()) {
                    list.addAll(ingredientsOpt.get());
                } else if (ingredientOpt.isPresent()) {
                    list.add(ingredientOpt.get());
                }
                return new FermentationRecipe(result, list, fluid, resultFluid, time);
            })
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<FluidStackData>> OPTIONAL_FLUID_STREAM_CODEC =
            FluidStackData.OPTIONAL_STREAM_CODEC.mapStream(buffer -> buffer);

    private static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<Ingredient>> INGREDIENTS_STREAM_CODEC =
            StreamCodec.of(
                    (buf, list) -> {
                        buf.writeVarInt(list.size());
                        for (Ingredient ing : list) {
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                        }
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        NonNullList<Ingredient> list = NonNullList.withSize(size, Ingredient.EMPTY);
                        for (int i = 0; i < size; i++) {
                            list.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                        }
                        return list;
                    }
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, FermentationRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            FermentationRecipe::getOutput,
            INGREDIENTS_STREAM_CODEC,
            FermentationRecipe::getIngredients,
            OPTIONAL_FLUID_STREAM_CODEC,
            FermentationRecipe::getFluid,
            OPTIONAL_FLUID_STREAM_CODEC,
            FermentationRecipe::getResultFluid,
            ByteBufCodecs.VAR_INT,
            FermentationRecipe::getFermentationTime,
            FermentationRecipe::new
    );

    @Override
    public MapCodec<FermentationRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, FermentationRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}

