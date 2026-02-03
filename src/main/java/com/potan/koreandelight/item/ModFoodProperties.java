package com.potan.koreandelight.item;

import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties KIMCHI_FOOD = new FoodProperties.Builder()
            .nutrition(6)
            .saturationMod(0.6f)
            .build();

    public static final FoodProperties FRESH_KIMCHI = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(0.4f)
            .build();

    public static FoodProperties KIMCHI_CABBAGE_FOOD = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.3f)
            .build();

    public static FoodProperties RED_PEPPER = new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(0.8f)
            .build();

    public static FoodProperties BEAN = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.3f)
            .build();

    public static FoodProperties GREEN_ONION = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.3f)
            .build();

    public static FoodProperties DOENJANG = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.3f)
            .build();
}
