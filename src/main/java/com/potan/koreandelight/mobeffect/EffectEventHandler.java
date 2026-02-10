package com.potan.koreandelight.mobeffect;

import com.potan.koreandelight.Koreandelight;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Koreandelight.MODID)
public class EffectEventHandler {
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntity().hasEffect(ModEffects.SPICY.get())) {
            int amplifier = event.getEntity().getEffect(ModEffects.SPICY.get()).getAmplifier();

            // 성곱함의 절반인 레벨당 10%의 속도 증가
            float bonus = 1.0f + (0.1f * (amplifier + 1));
            event.setNewSpeed(event.getOriginalSpeed() * bonus);
        }

    }
}
