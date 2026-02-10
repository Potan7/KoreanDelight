package com.potan.koreandelight.mobeffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SpicyEffect extends MobEffect {
    public SpicyEffect(MobEffectCategory category, int color) {
        super(category, color);

        // 신속 (레벨당 20%)의 절반인 10% 속도 증가
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                "91AEAA56-376B-4498-935B-2F7F68070635",
                0.1,
                AttributeModifier.Operation.MULTIPLY_BASE);

        // 성급함 (레벨당 10%)의 절반인 5% 공격 속도 증가
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                "7107DE5E-7CE8-4030-940E-514C1F160890",
                0.05,
                AttributeModifier.Operation.MULTIPLY_BASE);

    }
}
