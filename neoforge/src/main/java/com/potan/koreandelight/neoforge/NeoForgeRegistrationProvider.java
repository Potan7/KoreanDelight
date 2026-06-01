package com.potan.koreandelight.neoforge;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.platform.RegistrationProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class NeoForgeRegistrationProvider<T> implements RegistrationProvider<T> {
    private final DeferredRegister<T> register;

    public NeoForgeRegistrationProvider(ResourceKey<? extends Registry<T>> key) {
        this.register = DeferredRegister.create(key, Koreandelight.MODID);
    }

    @Override
    public <I extends T> Supplier<I> register(String name, Supplier<I> supplier) {
        return register.<I>register(name, supplier);
    }

    @Override
    public <I extends T> Holder<I> registerHolder(String name, Supplier<I> supplier) {
        return (Holder<I>) register.<I>register(name, supplier);
    }

    public DeferredRegister<T> getRegister() {
        return register;
    }
}
