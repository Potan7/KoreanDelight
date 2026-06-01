package com.potan.koreandelight.neoforge.event;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.mobeffect.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;

/**
 * Korean Delight 모드의 특수 효과 및 이벤트를 처리하는 핸들러 클래스입니다.
 * NeoForge의 이벤트 시스템을 사용하여 게임 내 다양한 시점에서 코드를 실행합니다.
 * EventBusSubscriber를 통해 NeoForge의 GAME 이벤트 버스에 자동으로 등록됩니다.
 */
@EventBusSubscriber(modid = Koreandelight.MODID, bus = EventBusSubscriber.Bus.GAME)
public class EffectEventHandler {

    /**
     * 아이템의 툴팁을 동적으로 설정하는 이벤트 핸들러입니다.
     * 1.21.1 마인크래프트의 Data Components 시스템을 사용하여 아이템에 저장된
     * CustomData(구 NBT) 내의 김치 정보("IsKimchi", "SpicyLevel")를 읽어와 툴팁에 표시합니다.
     *
     * @param event 아이템 툴팁 이벤트 객체
     */
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        
        // 1.21.1 사양: 아이템의 CUSTOM_DATA 컴포넌트(구 NBT 태그 역할)를 가져옵니다.
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            // 해당 아이템이 김치인지 확인
            if (tag.contains("IsKimchi") && tag.getBoolean("IsKimchi")) {
                int level = tag.getInt("SpicyLevel");
                List<Component> tooltip = event.getToolTip();
                
                // 김치 정보 및 매운맛 강도를 툴팁 목록에 빨간색으로 추가합니다.
                tooltip.add(Component.translatable("tooltip.koreandelight.is_kimchi").withStyle(ChatFormatting.RED));
                tooltip.add(Component.translatable("tooltip.koreandelight.spicy_level", level).withStyle(ChatFormatting.DARK_RED));
            }
        }
    }

    /**
     * 엔티티가 음식을 다 먹었을 때 실행되는 이벤트 핸들러입니다.
     * 플레이어가 김치류 음식을 섭취 완료했을 때, 섭취한 김치의 매운 강도에 비례하여
     * '매움(Spicy)' 효과 버프를 부여합니다.
     *
     * @param event 아이템 사용 완료 이벤트 객체
     */
    @SubscribeEvent
    public static void onFoodEat(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem();
        LivingEntity entity = event.getEntity();

        // 1.21.1 사양: 먹은 아이템이 비어있지 않고 CUSTOM_DATA 컴포넌트를 가지고 있는지 확인
        if (!stack.isEmpty()) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag tag = customData.copyTag();
                // 김치 정보 확인
                if (tag.contains("IsKimchi") && tag.getBoolean("IsKimchi")) {
                    int spicyLevel = tag.getInt("SpicyLevel");
                    if (spicyLevel > 0) {
                        // 매움 효과(SPICY)를 부여합니다.
                        // 지속 시간: 매운 등급당 20초 (400 틱 = 20초 * 20틱/초)
                        // 강도(Amplifier): spicyLevel - 1 (0부터 시작하므로 레벨 1은 0, 레벨 2는 1...)
                        entity.addEffect(new MobEffectInstance(ModEffects.SPICY_HOLDER, 400 * spicyLevel, spicyLevel - 1));
                    }
                }
            }
        }
    }

    /**
     * 플레이어의 블록 채굴 속도를 조절하는 이벤트 핸들러입니다.
     * 플레이어가 '매움(Spicy)' 효과를 받고 있을 때, 매움 버프의 레벨에 비례하여
     * 블록을 캐는 속도를 증가시킵니다. (레벨당 약 10% 속도 보너스 부여)
     *
     * @param event 플레이어 채굴 속도 이벤트 객체
     */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        // 플레이어가 매움 효과를 가지고 있는지 확인
        if (event.getEntity().hasEffect(ModEffects.SPICY_HOLDER)) {
            // 매움 효과의 앰플리파이어(증폭 등급)를 가져옵니다.
            int amplifier = event.getEntity().getEffect(ModEffects.SPICY_HOLDER).getAmplifier();

            // 성급함(Haste) 효과의 약 절반에 해당하는 보너스: 레벨당 10% 속도 증가
            float bonus = 1.0f + (0.1f * (amplifier + 1));
            // 새로운 채굴 속도를 설정합니다.
            event.setNewSpeed(event.getOriginalSpeed() * bonus);
        }
    }
}
