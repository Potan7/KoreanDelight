package com.potan.koreandelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.potan.koreandelight.block.blockentity.KimjangBasinBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class KimjangBasinRenderer implements BlockEntityRenderer<KimjangBasinBlockEntity> {
    private final ItemRenderer renderer;

    public KimjangBasinRenderer(BlockEntityRendererProvider.Context context) {
        renderer = context.getItemRenderer();
    }

    @Override
    public AABB getRenderBoundingBox(KimjangBasinBlockEntity basin) {
        var pos = basin.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 2, pos.getY() + 1, pos.getZ() + 2);
    }

    @Override
    public void render(KimjangBasinBlockEntity basin, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        ItemStack stack = basin.cabbage();
        for (int i = 0; i < Math.min(stack.getCount(), 4); i++) {
            pose.pushPose();
            pose.translate(0.55 + i % 2 * 0.9, 0.22, 0.55 + i / 2 * 0.9);
            pose.mulPose(Axis.YP.rotationDegrees(20 + i * 65));
            pose.mulPose(Axis.XP.rotationDegrees(90));
            pose.scale(0.62F, 0.62F, 0.62F);
            renderer.renderStatic(stack, ItemDisplayContext.NONE, light, overlay, pose, buffers, basin.getLevel(), i);
            pose.popPose();
        }
    }
}
