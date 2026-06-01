package com.potan.koreandelight.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface IPlatformHelper {
    String getPlatformName();
    boolean isDevelopmentEnvironment();

    /**
     * 주어진 레지스트리 키에 대한 RegistrationProvider를 반환합니다.
     */
    <T> RegistrationProvider<T> getProvider(ResourceKey<? extends Registry<T>> registryKey);
}
