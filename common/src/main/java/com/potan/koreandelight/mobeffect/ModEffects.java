package com.potan.koreandelight.mobeffect;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.function.Supplier;

public class ModEffects {
    public static final RegistrationProvider<MobEffect> EFFECTS = Services.PLATFORM.getProvider(Registries.MOB_EFFECT);

    // 매운맛 효과
    public static final Holder<MobEffect> SPICY_HOLDER = EFFECTS.registerHolder(
            "spicy",
            () -> new SpicyEffect(MobEffectCategory.BENEFICIAL, 0xFF4500)
    );

    public static final Supplier<MobEffect> SPICY = () -> SPICY_HOLDER.value();

    public static void init() {
        // 클래스 로딩을 위해 호출됩니다.
    }
}
