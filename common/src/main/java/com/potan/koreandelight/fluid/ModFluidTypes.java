package com.potan.koreandelight.fluid;

import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
    public static final ResourceLocation WATER_FLOW = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");
    public static final ResourceLocation WATER_OVERLAY = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_overlay");

    public static final RegistrationProvider<FluidType> FLUID_TYPES =
            Services.PLATFORM.getProvider(NeoForgeRegistries.Keys.FLUID_TYPES);

    public static final Supplier<FluidType> SOY_SAUCE_FLUID_TYPE = FLUID_TYPES.register(
            "soy_sauce_fluid",
            () -> new BaseFluidType(
                    WATER_STILL,
                    WATER_FLOW,
                    WATER_OVERLAY,
                    0xA1412010,
                    new Vector3f(65.0F / 255.0F, 32.0F / 255.0F, 16.0F / 255.0F),
                    FluidType.Properties.create()
                            .density(1100)
                            .viscosity(1500)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            )
    );

    public static void init() {
    }
}
