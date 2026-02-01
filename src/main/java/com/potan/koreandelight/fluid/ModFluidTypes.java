package com.potan.koreandelight.fluid;

import com.potan.koreandelight.Koreandelight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL_RL = ResourceLocation.fromNamespaceAndPath(Koreandelight.MODID, "water_still");
    public static final ResourceLocation WATER_FLOW_RL = ResourceLocation.fromNamespaceAndPath(Koreandelight.MODID, "water_flow");
    public static final ResourceLocation SOY_SAUCE_OVERLAY_RL = ResourceLocation.fromNamespaceAndPath(Koreandelight.MODID, "misc/in_soap_water"); // Assuming no specific overlay, or reuse water

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Koreandelight.MODID);

    public static final RegistryObject<FluidType> SOY_SAUCE_FLUID_TYPE = FLUID_TYPES.register("soy_sauce_fluid",
            () -> new BaseFluidType(WATER_STILL_RL, WATER_FLOW_RL, null, 0xA1412010, new Vector3f(65f / 255f, 32f / 255f, 16f / 255f),
                    FluidType.Properties.create()
                            .lightLevel(0)
                            .density(1100)
                            .viscosity(1500)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
