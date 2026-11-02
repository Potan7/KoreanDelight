package com.potan.koreandelight.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum FermentationEnvironment implements StringRepresentable {
    ANY, SUNNY, COOL;

    public static final Codec<FermentationEnvironment> CODEC = StringRepresentable.fromEnum(FermentationEnvironment::values);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String getTranslationKey() {
        return "gui.koreandelight.onggi.environment." + getSerializedName();
    }

    public double getProgressPerTick(boolean canSeeSky, boolean isDay, int brightness) {
        boolean preferred = switch (this) {
            case SUNNY -> canSeeSky && isDay;
            case COOL -> brightness <= 7;
            case ANY -> false;
        };
        return preferred ? 1.5 : 1.0;
    }
}
