package com.potan.koreandelight.fluid;

import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class ModFluids {
    public static final RegistrationProvider<Fluid> FLUIDS =
            Services.PLATFORM.getProvider(Registries.FLUID);

    // 간장 액체 (공통 인터페이스)
    public static final Supplier<FlowingFluid> SOURCE_SOY_SAUCE = FLUIDS.register("soy_sauce_fluid",
            SoySauceFluid.Source::new
    );

    public static final Supplier<FlowingFluid> FLOWING_SOY_SAUCE = FLUIDS.register("flowing_soy_sauce_fluid",
            SoySauceFluid.Flowing::new
    );

    public static void init() {
        // 클래스 로딩을 위해 호출됩니다.
    }
}
