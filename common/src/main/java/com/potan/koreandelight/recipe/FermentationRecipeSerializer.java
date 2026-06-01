package com.potan.koreandelight.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public class FermentationRecipeSerializer implements RecipeSerializer<FermentationRecipe> {
    private static final MapCodec<FermentationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(FermentationRecipe::getOutput),
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(FermentationRecipe::getInput),
                    FluidStackData.CODEC.optionalFieldOf("fluid").forGetter(FermentationRecipe::getFluid),
                    FluidStackData.CODEC.optionalFieldOf("result_fluid").forGetter(FermentationRecipe::getResultFluid),
                    Codec.INT.optionalFieldOf("fermentation_time", 200)
                            .validate(time -> time > 0
                                    ? DataResult.success(time)
                                    : DataResult.error(() -> "fermentation_time must be positive"))
                            .forGetter(FermentationRecipe::getFermentationTime)
            ).apply(inst, FermentationRecipe::new)
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<FluidStackData>> OPTIONAL_FLUID_STREAM_CODEC =
            FluidStackData.OPTIONAL_STREAM_CODEC.mapStream(buffer -> buffer);

    private static final StreamCodec<RegistryFriendlyByteBuf, FermentationRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            FermentationRecipe::getOutput,
            Ingredient.CONTENTS_STREAM_CODEC,
            FermentationRecipe::getInput,
            OPTIONAL_FLUID_STREAM_CODEC,
            FermentationRecipe::getFluid,
            OPTIONAL_FLUID_STREAM_CODEC,
            FermentationRecipe::getResultFluid,
            net.minecraft.network.codec.ByteBufCodecs.VAR_INT,
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
