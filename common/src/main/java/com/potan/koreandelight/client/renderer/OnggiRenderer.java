package com.potan.koreandelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/**
 * 옹기 블록 엔티티의 렌더러입니다.
 * 옹기 내부의 아이템 및 액체를 렌더링합니다.
 */
public class OnggiRenderer implements BlockEntityRenderer<OnggiBlockEntity> {

    public OnggiRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(OnggiBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        // 옹기 내부의 렌더링 로직 (아이템 등)
        // 1.21.1 Multiloader의 'common' 모듈에서는 플랫폼 중립적인 렌더링만 수행합니다.
        // 액체 렌더링은 각 플랫폼의 유체 핸들러를 통해 고도화될 예정입니다.
    }
}
