package com.potan.koreandelight.item;

import com.potan.koreandelight.mobeffect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    // 겉절이: 가볍게 매콤 (50% 확률)
    public static final FoodProperties FRESH_KIMCHI = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(0.4f)
            .effect(() -> new MobEffectInstance(ModEffects.SPICY.get(), 100, 0), 0.5f)
            .build();

    // 김치: 표준 매콤함 (100% 확률)
    public static final FoodProperties KIMCHI_FOOD = new FoodProperties.Builder()
            .nutrition(6)
            .saturationMod(0.6f)
            .effect(() -> new MobEffectInstance(ModEffects.SPICY.get(), 200, 0), 1.0f)
            .build();

    // 묵은지: 깊고 강한 매운맛
    public static final FoodProperties AGED_KIMCHI_FOOD = new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(0.8f)
            .effect(() -> new MobEffectInstance(ModEffects.SPICY.get(), 300, 1), 1.0f)
            .build();

    // 고추
    public static FoodProperties RED_PEPPER = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.8f)
            .effect(() -> new MobEffectInstance(ModEffects.SPICY.get(), 100, 2), 1.0f)
            .build();

    // 배추
    public static FoodProperties KIMCHI_CABBAGE_FOOD = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.3f)
            .build();

    // 콩
    public static FoodProperties BEAN = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.3f)
            .build();

    // 대파
    public static FoodProperties GREEN_ONION = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.3f)
            .build();

    // 된장
    public static FoodProperties DOENJANG = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.3f)
            .build();
}
