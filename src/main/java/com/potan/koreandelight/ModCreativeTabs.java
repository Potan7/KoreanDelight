package com.potan.koreandelight;

import com.potan.koreandelight.item.ModFoodItems;
import com.potan.koreandelight.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    // 크리에이티브 탭 레지스트리 생성
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Koreandelight.MODID);

    // 모든 아이템을 보여주는 탭 등록
    public static final Supplier<CreativeModeTab> ALL_ITEMS_TAB = CREATIVE_TABS.register("all_items_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModFoodItems.KIMCHI.get())) // 탭 아이콘으로 김치 사용
                    .title(Component.translatable("creativetab.koreandelight")) // 탭 이름 설정
                    .displayItems((parameters, output) -> {
                        // ModItems에 등록된 모든 아이템을 탭에 추가
                        ModItems.ITEMS.getEntries().forEach(itemRegistryObject -> output.accept(itemRegistryObject.get()));
                        // ModFoodItems에 등록된 모든 아이템을 탭에 추가
                        ModFoodItems.ITEMS.getEntries().forEach(itemRegistryObject -> output.accept(itemRegistryObject.get()));
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}