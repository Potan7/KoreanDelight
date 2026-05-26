package com.potan.koreandelight.neoforge;

import com.potan.koreandelight.platform.IPlatformHelper;
import com.potan.koreandelight.platform.RegistrationProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.loading.FMLLoader;

import java.util.HashMap;
import java.util.Map;

public class NeoForgePlatformHelper implements IPlatformHelper {
    private final Map<ResourceKey<?>, NeoForgeRegistrationProvider<?>> providers = new HashMap<>();

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> RegistrationProvider<T> getProvider(ResourceKey<? extends Registry<T>> registryKey) {
        return (RegistrationProvider<T>) providers.computeIfAbsent(registryKey, k -> new NeoForgeRegistrationProvider<>(registryKey));
    }

    public Map<ResourceKey<?>, NeoForgeRegistrationProvider<?>> getProviders() {
        return providers;
    }
}
