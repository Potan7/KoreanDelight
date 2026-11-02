package com.potan.koreandelight.neoforge;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.data.ModRecipeProvider;
import com.potan.koreandelight.item.ModFoodItems;
import com.potan.koreandelight.item.ModItems;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import java.util.Optional;

@Mod(Koreandelight.MODID)
public class KoreandelightNeoForge {
    public KoreandelightNeoForge(IEventBus modEventBus) {
        // 1. 공통(Common) 모듈의 초기화 (레지스터 객체들 생성)
        Koreandelight.init();

        // 2. NeoForge 전용 플랫폼 헬퍼에서 관리하는 모든 DeferredRegister를 이벤트 버스에 등록
        // 이 과정이 누락되거나 순서가 잘못되면 "Trying to access unbound value" 오류가 발생합니다.
        if (Services.PLATFORM instanceof NeoForgePlatformHelper helper) {
            helper.getProviders().values().forEach(provider -> {
                provider.getRegister().register(modEventBus);
            });
        }

        modEventBus.addListener(KoreandelightNeoForge::gatherData);
        modEventBus.addListener(KoreandelightNeoForge::modifyDefaultComponents);
    }

    private static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        FoodProperties rice = vectorwing.farmersdelight.common.registry.ModItems.COOKED_RICE.get()
                .components().get(DataComponents.FOOD);
        FoodProperties bowlRice = new FoodProperties(rice.nutrition(), rice.saturation(), rice.canAlwaysEat(),
                rice.eatSeconds(), Optional.of(new ItemStack(ModItems.RICE_BOWL.get())), rice.effects());
        event.modify(ModFoodItems.COOKED_RICE_BOWL.get(), components -> components.set(DataComponents.FOOD, bowlRice));
    }

    private static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new ModRecipeProvider(event.getGenerator().getPackOutput(), event.getLookupProvider())
        );
    }
}
