package com.potan.koreandelight.mobeffect;

import com.potan.koreandelight.Koreandelight;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Koreandelight.MODID)
public class EffectEventHandler {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.getBoolean("IsKimchi")) {
                int level = tag.getInt("SpicyLevel");
                List<Component> tooltip = event.getToolTip();
                
                // 김치 정보 추가
                tooltip.add(Component.translatable("tooltip.koreandelight.is_kimchi").withStyle(ChatFormatting.RED));
                tooltip.add(Component.translatable("tooltip.koreandelight.spicy_level", level).withStyle(ChatFormatting.DARK_RED));
            }
        }
    }

    @SubscribeEvent
    public static void onFoodEat(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem();
        LivingEntity entity = event.getEntity();

        // 김치 NBT가 있는지 확인
        if (!stack.isEmpty() && stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.getBoolean("IsKimchi")) {
                int spicyLevel = tag.getInt("SpicyLevel");
                if (spicyLevel > 0) {
                    // 매움 효과 부여 (레벨당 20초, 앰플리파이어는 spicyLevel - 1)
                    entity.addEffect(new MobEffectInstance(ModEffects.SPICY.get(), 400 * spicyLevel, spicyLevel - 1));
                }
            }
        }
    }

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
