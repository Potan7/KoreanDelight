package com.potan.koreandelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.potan.koreandelight.block.blockentity.MortarBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;

public class MortarRenderer implements BlockEntityRenderer<MortarBlockEntity> {
    private final ItemRenderer renderer;
    public MortarRenderer(BlockEntityRendererProvider.Context context) { renderer = context.getItemRenderer(); }
    @Override public void render(MortarBlockEntity mortar, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        if (mortar.getStack().isEmpty()) return;
        pose.pushPose(); pose.translate(0.5, 0.4, 0.5); pose.mulPose(Axis.XP.rotationDegrees(90)); pose.scale(0.45F, 0.45F, 0.45F);
        renderer.renderStatic(mortar.getStack(), ItemDisplayContext.FIXED, light, overlay, pose, buffers, mortar.getLevel(), 0);
        pose.popPose();
    }
}
