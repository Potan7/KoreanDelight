package com.potan.koreandelight.fluid;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.item.ModItems;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, Koreandelight.MODID);

    public static final RegistryObject<FlowingFluid> SOURCE_SOY_SAUCE = FLUIDS.register("soy_sauce_fluid",
            () -> new ForgeFlowingFluid.Source(ModFluids.SOY_SAUCE_FLUID_PROPERTIES));

    public static final RegistryObject<FlowingFluid> FLOWING_SOY_SAUCE = FLUIDS.register("flowing_soy_sauce_fluid",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.SOY_SAUCE_FLUID_PROPERTIES));


    public static final ForgeFlowingFluid.Properties SOY_SAUCE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.SOY_SAUCE_FLUID_TYPE, SOURCE_SOY_SAUCE, FLOWING_SOY_SAUCE)
            .slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.SOY_SAUCE_BLOCK)
            .bucket(ModItems.SOY_SAUCE_BUCKET);


    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
