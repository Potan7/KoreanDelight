package com.potan.koreandelight.mobeffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceLocation;

public class SpicyEffect extends MobEffect {
    public SpicyEffect(MobEffectCategory category, int color) {
        super(category, color);
        
        // 1.21.1에서는 AttributeModifier의 Operation 이름이 변경되었습니다.
        // MULTIPLY_BASE -> ADD_MULTIPLIED_BASE
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE,
                ResourceLocation.fromNamespaceAndPath("koreandelight", "effect.spicy.attack_damage"),
                0.2D,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath("koreandelight", "effect.spicy.movement_speed"),
                0.1D,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }
}
