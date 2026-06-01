package com.potan.koreandelight.platform;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public interface RegistrationProvider<T> {
    <I extends T> Supplier<I> register(String name, Supplier<I> supplier);

    /**
     * 등록된 객체를 Holder 형태로 가져옵니다. (1.21.1 대응)
     */
    default <I extends T> Holder<I> registerHolder(String name, Supplier<I> supplier) {
        // 기본 구현은 플랫폼별 구현체에서 오버라이드해야 정확한 Holder를 반환할 수 있습니다.
        throw new UnsupportedOperationException("Holder registration not implemented for this platform provider");
    }
}
