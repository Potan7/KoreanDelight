package com.potan.koreandelight.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

public class KimjangRecipeSerializer implements RecipeSerializer<KimjangRecipe> {
    private static final MapCodec<KimjangRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("vegetable").forGetter(KimjangRecipe::getVegetable),
                    Ingredient.CODEC_NONEMPTY.listOf().fieldOf("seasonings")
                            .flatXmap(
                                    seasonings -> seasonings.isEmpty()
                                            ? DataResult.error(() -> "Kimjang recipe requires at least one seasoning")
                                            : DataResult.success(seasonings),
                                    DataResult::success
                            )
                            .forGetter(KimjangRecipe::getSeasonings),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(KimjangRecipe::getOutput),
                    Codec.intRange(1, 4).fieldOf("seasoning_count").forGetter(KimjangRecipe::getSeasoningCount)
            ).apply(inst, KimjangRecipe::new)
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> SEASONINGS_STREAM_CODEC =
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(4));

    private static final StreamCodec<RegistryFriendlyByteBuf, KimjangRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            KimjangRecipe::getVegetable,
            SEASONINGS_STREAM_CODEC,
            KimjangRecipe::getSeasonings,
            ItemStack.STREAM_CODEC,
            KimjangRecipe::getOutput,
            ByteBufCodecs.VAR_INT,
            KimjangRecipe::getSeasoningCount,
            KimjangRecipe::new
    );

    @Override
    public MapCodec<KimjangRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, KimjangRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
