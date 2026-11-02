package com.potan.koreandelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.potan.koreandelight.block.blockentity.RiceBowlBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RiceBowlRenderer implements BlockEntityRenderer<RiceBowlBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public RiceBowlRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(RiceBowlBlockEntity bowl, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        Vec3 offset = bowl.getPlacementOffset();
        BlockState state = bowl.getBlockState();
        pose.pushPose();
        pose.translate(offset.x, 0, offset.z);
        blockRenderer.getModelRenderer().renderModel(pose.last(), buffers.getBuffer(RenderType.cutout()), state,
                blockRenderer.getBlockModel(state), 1, 1, 1, packedLight, packedOverlay);
        pose.popPose();
    }
}
