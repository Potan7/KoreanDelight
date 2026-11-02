package com.potan.koreandelight;

import com.potan.koreandelight.item.ModFoodItems;
import com.potan.koreandelight.item.ModItems;
import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final RegistrationProvider<CreativeModeTab> CREATIVE_TABS =
            Services.PLATFORM.getProvider(Registries.CREATIVE_MODE_TAB);

    public static final Supplier<CreativeModeTab> KOREAN_DELIGHT_TAB = CREATIVE_TABS.register(
            "koreandelight_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModFoodItems.KIMCHI.get()))
                    .title(Component.translatable("creativetab.koreandelight"))
                    .displayItems((parameters, output) -> {
                        // 아이템 및 블록 추가
                        output.accept(ModItems.SOY_SAUCE_BUCKET.get());
                        output.accept(ModItems.SOY_SAUCE_BOTTLE.get());
                        output.accept(ModItems.FISH_SAUCE_BUCKET.get());
                        output.accept(ModItems.KIMCHI_CABBAGE_SEEDS.get());
                        output.accept(ModItems.RED_PEPPER_SEEDS.get());
                        output.accept(ModItems.GREEN_ONION_SEEDS.get());
                        output.accept(ModItems.GARLIC.get());
                        output.accept(ModItems.GOCHUJANG.get());
                        output.accept(ModItems.FISH_SAUCE.get());
                        output.accept(ModItems.MINCED_GARLIC.get());
                        output.accept(ModItems.TTEOK.get());
                        output.accept(ModItems.MINCED_FISH.get());
                        output.accept(ModItems.RAW_FISH_CAKE.get());
                        output.accept(ModItems.RAW_KIMCHI_JEON.get());
                        output.accept(ModItems.RAW_PAJEON.get());
                        output.accept(ModItems.RED_PEPPER_POWDER.get());
                        output.accept(ModItems.PESTLE.get());
                        output.accept(ModItems.SALT.get());
                        output.accept(ModItems.DRIED_RED_PEPPER.get());
                        output.accept(ModItems.BOILED_BEANS.get());
                        output.accept(ModItems.MEJU_LUMP.get());
                        output.accept(ModItems.MEJU_POWDER.get());
                        output.accept(ModItems.GLUTINOUS_RICE.get());
                        output.accept(ModItems.STEAMED_RICE.get());
                        output.accept(ModItems.STEAMED_GLUTINOUS_RICE.get());
                        output.accept(ModItems.GLUTINOUS_RICE_PASTE.get());
                        output.accept(ModItems.RICE_FLOUR.get());
                        output.accept(ModItems.RICE_CAKE_DOUGH.get());
                        output.accept(ModItems.SESAME.get());
                        output.accept(ModItems.ROASTED_SESAME.get());
                        output.accept(ModItems.SESAME_OIL.get());
                        output.accept(ModItems.BRINED_CABBAGE.get());
                        output.accept(ModItems.RAW_SHRIMP.get());
                        output.accept(ModItems.ANCHOVY.get());
                        output.accept(ModItems.SALTED_SHRIMP.get());
                        output.accept(ModItems.TOFU.get());
                        output.accept(ModItems.ONGGI_BLOCK_ITEM.get());
                        output.accept(ModItems.MEJU_BLOCK_ITEM.get());
                        output.accept(ModItems.FERMENTED_MEJU.get());
                        output.accept(ModItems.RICE_BOWL.get());
                        output.accept(ModItems.LETTUCE_SEEDS.get());
                        output.accept(ModItems.PERILLA_SEEDS.get());
                        output.accept(ModFoodItems.LETTUCE.get());
                        output.accept(ModFoodItems.SSAM.get());
                        output.accept(ModFoodItems.SSAMJANG.get());
                        output.accept(ModFoodItems.GARLIC_BOWL.get());
                        output.accept(ModFoodItems.SSAMJANG_BOWL.get());
                        output.accept(ModFoodItems.KIMCHI_BOWL.get());
                        output.accept(ModFoodItems.LETTUCE_BOWL.get());
                        output.accept(ModFoodItems.PERILLA_LEAF_BOWL.get());

                        // 음식류 추가
                        output.accept(ModFoodItems.COOKED_RICE_BOWL.get());
                        output.accept(ModFoodItems.KIMCHI_CABBAGE.get());
                        output.accept(ModFoodItems.RED_PEPPER.get());
                        output.accept(ModFoodItems.BEAN.get());
                        output.accept(ModFoodItems.GREEN_ONION.get());
                        output.accept(ModFoodItems.KIMCHI.get());
                        output.accept(ModFoodItems.AGED_KIMCHI.get());
                        output.accept(ModFoodItems.DOENJANG.get());
                        output.accept(ModFoodItems.DOENJANG_BLOCK_ITEM.get());
                        output.accept(ModFoodItems.FISH_CAKE.get());
                        output.accept(ModFoodItems.KIMCHI_JEON.get());
                        output.accept(ModFoodItems.PAJEON.get());
                        output.accept(ModFoodItems.BIBIMBAP.get());
                        output.accept(ModFoodItems.CHICKEN_KALGUKSU.get());
                        output.accept(ModFoodItems.TTEOKBOKKI.get());
                        output.accept(ModFoodItems.JEYUK_BOKKEUM.get());
                        output.accept(ModFoodItems.PORK_GUKBAP.get());
                        output.accept(ModFoodItems.DOENJANG_JJIGAE.get());
                        output.accept(ModFoodItems.SIKHYE.get());
                        output.accept(ModFoodItems.RADISH.get());
                        output.accept(ModFoodItems.PERILLA_LEAF.get());
                        output.accept(ModFoodItems.FRESH_KIMCHI.get());
                        output.accept(ModFoodItems.GRILLED_KIMCHI.get());
                        output.accept(ModFoodItems.GRILLED_GARLIC.get());
                        output.accept(ModFoodItems.SUNGNYUNG.get());
                        output.accept(ModFoodItems.DONGCHIMI.get());
                        output.accept(ModFoodItems.STEAMED_EGG.get());
                    }).build()
    );

    public static void init() {
        // 클래스 로딩을 위해 호출됩니다.
    }
}
