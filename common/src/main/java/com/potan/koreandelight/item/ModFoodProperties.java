package com.potan.koreandelight.item;

import com.potan.koreandelight.mobeffect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties GARLIC = food(2, 0.2f);
    public static final FoodProperties FISH_CAKE = food(5, 0.6f);
    public static final FoodProperties KIMCHI_JEON = food(7, 0.7f);
    public static final FoodProperties PAJEON = food(7, 0.7f);
    public static final FoodProperties BIBIMBAP = food(12, 0.9f);
    public static final FoodProperties CHICKEN_KALGUKSU = food(14, 1.0f);
    public static final FoodProperties TTEOKBOKKI = food(12, 0.8f);
    public static final FoodProperties JEYUK_BOKKEUM = food(14, 0.9f);
    public static final FoodProperties PORK_GUKBAP = food(14, 1.0f);
    public static final FoodProperties DOENJANG_GUKBAP = food(14, 1.0f);
    public static final FoodProperties SIKHYE = food(4, 0.3f);

    // 김치: 표준 매콤함 (100% 확률)
    public static final FoodProperties KIMCHI_FOOD = new FoodProperties.Builder()
            .nutrition(6)
            .saturationModifier(0.6f)
            .effect(() -> new MobEffectInstance(ModEffects.SPICY_HOLDER, 200, 0), 1.0f)
            .build();

    // 묵은지: 깊고 강한 매운맛
    public static final FoodProperties AGED_KIMCHI_FOOD = new FoodProperties.Builder()
            .nutrition(8)
            .saturationModifier(0.8f)
            .effect(() -> new MobEffectInstance(ModEffects.SPICY_HOLDER, 300, 1), 1.0f)
            .build();

    // 고추
    public static final FoodProperties RED_PEPPER = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.8f)
            .effect(() -> new MobEffectInstance(ModEffects.SPICY_HOLDER, 100, 2), 1.0f)
            .build();

    // 배추
    public static final FoodProperties KIMCHI_CABBAGE_FOOD = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3f)
            .build();

    // 콩
    public static final FoodProperties BEAN = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3f)
            .build();

    // 대파
    public static final FoodProperties GREEN_ONION = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3f)
            .build();

    // 된장
    public static final FoodProperties DOENJANG = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3f)
            .build();

    private static FoodProperties food(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
    }
}
