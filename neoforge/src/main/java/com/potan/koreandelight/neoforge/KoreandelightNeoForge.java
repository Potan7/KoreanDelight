package com.potan.koreandelight.neoforge;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.data.ModRecipeProvider;
import com.potan.koreandelight.platform.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

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
    }

    private static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new ModRecipeProvider(event.getGenerator().getPackOutput(), event.getLookupProvider())
        );
    }
}
