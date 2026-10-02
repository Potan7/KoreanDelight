package com.potan.koreandelight.mobeffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceLocation;

public class SpicyEffect extends MobEffect {
    public SpicyEffect(MobEffectCategory category, int color) {
        super(category, color);

        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath("koreandelight", "effect.spicy.movement_speed"),
                0.05D,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }
}
