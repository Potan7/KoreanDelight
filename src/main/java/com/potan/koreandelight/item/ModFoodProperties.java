package com.potan.koreandelight.item;

import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    // 김치
    public static final FoodProperties KIMCHI_FOOD = new FoodProperties.Builder()
            .nutrition(6)
            .saturationMod(0.6f)
            .build();

    // 묵은지
    public static final FoodProperties AGED_KIMCHI_FOOD = new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(0.8f)
            .build();

    // 겉절이
    public static final FoodProperties FRESH_KIMCHI = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(0.4f)
            .build();

    // 배추
    public static FoodProperties KIMCHI_CABBAGE_FOOD = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.3f)
            .build();

    // 고추
    public static FoodProperties RED_PEPPER = new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(0.8f)
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
