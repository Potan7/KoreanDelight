package com.potan.koreandelight.menu;

import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class ModMenus {
    public static final RegistrationProvider<MenuType<?>> MENUS = Services.PLATFORM.getProvider(Registries.MENU);
    public static final Supplier<MenuType<OnggiMenu>> ONGGI = MENUS.register(
            "onggi", () -> new MenuType<>(OnggiMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void init() {
    }
}
