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
                        output.accept(ModItems.KIMCHI_CABBAGE_SEEDS.get());
                        output.accept(ModItems.RED_PEPPER_SEEDS.get());
                        output.accept(ModItems.BEAN_SEEDS.get());
                        output.accept(ModItems.GREEN_ONION_SEEDS.get());
                        output.accept(ModItems.RED_PEPPER_POWDER.get());
                        output.accept(ModItems.ONGGI_BLOCK_ITEM.get());
                        output.accept(ModItems.MEJU_BLOCK_ITEM.get());
                        output.accept(ModItems.FERMENTED_MEJU.get());
                        output.accept(ModItems.KIMJANG_BASIN.get());

                        // 음식류 추가
                        output.accept(ModFoodItems.KIMCHI_CABBAGE.get());
                        output.accept(ModFoodItems.RED_PEPPER.get());
                        output.accept(ModFoodItems.BEAN.get());
                        output.accept(ModFoodItems.GREEN_ONION.get());
                        output.accept(ModFoodItems.KIMCHI.get());
                        output.accept(ModFoodItems.AGED_KIMCHI.get());
                        output.accept(ModFoodItems.DOENJANG.get());
                        output.accept(ModFoodItems.DOENJANG_BLOCK_ITEM.get());
                    }).build()
    );

    public static void init() {
        // 클래스 로딩을 위해 호출됩니다.
    }
}
