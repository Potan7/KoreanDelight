package com.potan.koreandelight.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;

/**
 * Common recipe-side fluid data without depending on a platform FluidStack type.
 */
public record FluidStackData(Fluid fluid, int amount) {
    public static final Codec<FluidStackData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidStackData::fluid),
                    Codec.INT.optionalFieldOf("amount", 1000).forGetter(FluidStackData::amount)
            ).apply(instance, FluidStackData::new)
    );

    public static final StreamCodec<FriendlyByteBuf, FluidStackData> STREAM_CODEC = StreamCodec.of(
            (buffer, stack) -> {
                ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(stack.fluid());
                ResourceLocation.STREAM_CODEC.encode(buffer, fluidId);
                buffer.writeVarInt(stack.amount());
            },
            buffer -> {
                ResourceLocation fluidId = ResourceLocation.STREAM_CODEC.decode(buffer);
                Fluid fluid = BuiltInRegistries.FLUID.getOptional(fluidId).orElse(Fluids.EMPTY);
                return new FluidStackData(fluid, buffer.readVarInt());
            }
    );

    public static final StreamCodec<FriendlyByteBuf, Optional<FluidStackData>> OPTIONAL_STREAM_CODEC = StreamCodec.of(
            (buffer, stack) -> {
                buffer.writeBoolean(stack.isPresent());
                stack.ifPresent(value -> STREAM_CODEC.encode(buffer, value));
            },
            buffer -> buffer.readBoolean() ? Optional.of(STREAM_CODEC.decode(buffer)) : Optional.empty()
    );

    public boolean isEmpty() {
        return this.fluid == Fluids.EMPTY || this.amount <= 0;
    }
}
