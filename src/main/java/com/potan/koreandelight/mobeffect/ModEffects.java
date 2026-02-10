package com.potan.koreandelight.mobeffect;

import com.potan.koreandelight.Koreandelight;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Koreandelight.MODID);

    public static final RegistryObject<MobEffect> SPICY = MOB_EFFECTS.register("spicy_effect",
            () -> new SpicyEffect(MobEffectCategory.BENEFICIAL, 0xFF4500));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
