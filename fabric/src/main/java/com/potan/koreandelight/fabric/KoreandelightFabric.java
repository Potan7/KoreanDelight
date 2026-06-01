package com.potan.koreandelight.fabric;

import com.potan.koreandelight.Koreandelight;
import net.fabricmc.api.ModInitializer;

public class KoreandelightFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // 공통 초기화 로직 호출
        Koreandelight.init();
    }
}
