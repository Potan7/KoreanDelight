package com.potan.koreandelight.item;

import com.potan.koreandelight.mobeffect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class ModFoodProperties {
    public static final FoodProperties GARLIC = food(2, 0.2f);
    public static final FoodProperties LETTUCE = food(2, 0.3f);
    public static final FoodProperties SSAM = ssam(LETTUCE);
    public static final FoodProperties SSAMJANG = food(2, 0.3f);
    public static final FoodProperties KIMCHI_JEON = food(7, 0.7f);
    public static final FoodProperties BIBIMBAP = food(12, 0.9f);
    public static final FoodProperties CHICKEN_KALGUKSU = food(14, 1.0f);
    public static final FoodProperties TTEOKBOKKI = food(12, 0.8f);
    public static final FoodProperties JEYUK_BOKKEUM = food(14, 0.9f);
    public static final FoodProperties PORK_GUKBAP = food(14, 1.0f);
    public static final FoodProperties DOENJANG_JJIGAE = food(14, 1.0f);
    public static final FoodProperties SIKHYE = food(4, 0.3f);

    // shortcut: 신규 음식 영양치는 임시값이며, 1회분 배분과 중화 효과 구현 시 조정합니다.
    public static final FoodProperties RADISH = food(2, 0.3f);
    public static final FoodProperties PERILLA_LEAF = food(2, 0.3f);
    public static final FoodProperties FRESH_KIMCHI = food(4, 0.4f);
    public static final FoodProperties GRILLED_KIMCHI = food(7, 0.7f);
    public static final FoodProperties GRILLED_GARLIC = food(3, 0.3f);
    public static final FoodProperties SUNGNYUNG = food(4, 0.3f);
    public static final FoodProperties DONGCHIMI = food(4, 0.4f);
    public static final FoodProperties STEAMED_EGG = food(6, 0.6f);

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

    // 반환 스택은 빈 밥그릇 아이템이 등록된 뒤에 생성합니다.
    public static FoodProperties cookedRiceBowl() {
        return new FoodProperties.Builder().nutrition(6).saturationModifier(0.4F)
                .usingConvertsTo(ModItems.RICE_BOWL.get()).build();
    }

    private static FoodProperties food(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
    }

    public static FoodProperties ssam(FoodProperties... ingredients) {
        int nutrition = 0;
        float saturation = 0;
        List<FoodProperties.PossibleEffect> effects = new ArrayList<>();
        for (FoodProperties ingredient : ingredients) {
            nutrition += ingredient.nutrition();
            saturation += ingredient.saturation();
            effects.addAll(ingredient.effects());
        }
        // The first entry is the outer leaf, which does not count as a filling.
        if (ingredients.length >= 4) {
            saturation *= 1.2F;
        }
        return new FoodProperties(nutrition, saturation, false, 1.6F, Optional.empty(), List.copyOf(effects));
    }

    public static FoodProperties servedIn(FoodProperties food, ItemLike container) {
        return new FoodProperties(food.nutrition(), food.saturation(), food.canAlwaysEat(), food.eatSeconds(),
                Optional.of(new ItemStack(container)), food.effects());
    }
}
