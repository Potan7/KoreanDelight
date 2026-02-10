package com.potan.koreandelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.potan.koreandelight.block.custom.OnggiBlock;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class OnggiRenderer implements BlockEntityRenderer<OnggiBlockEntity> {
    public OnggiRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(OnggiBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (state.hasProperty(OnggiBlock.HAS_LID) && state.getValue(OnggiBlock.HAS_LID)) {
            return;
        }

        ItemStack itemStack = blockEntity.getInventory().getStackInSlot(0);
        FluidStack fluidStack = blockEntity.getFluidTank().getFluid();

        // 1. 액체 렌더링
        if (!fluidStack.isEmpty()) {
            renderFluid(blockEntity, fluidStack, poseStack, bufferSource, combinedLight);
        }

        // 2. 아이템 렌더링
        if (!itemStack.isEmpty()) {
            renderItem(itemStack, poseStack, bufferSource, combinedLight, combinedOverlay);
        }
    }

    private void renderItem(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        
        // 옹기 중앙 바닥보다 살짝 위로 이동
        poseStack.translate(0.5, 0.2, 0.5);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        
        // 아이템이 옹기 안에 평평하게 놓인 것처럼 보이게 회전
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, null, 0);
        
        poseStack.popPose();
    }

    private void renderFluid(OnggiBlockEntity blockEntity, FluidStack fluidStack, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight) {
        poseStack.pushPose();

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(clientFluid.getStillTexture(fluidStack));
        int color = clientFluid.getTintColor(fluidStack);

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        // 액체 양에 따른 높이 계산 (1000mB가 가득 찬 상태)
        float fluidHeight = 0.125f + (blockEntity.getFluidTank().getFluidAmount() / (float) blockEntity.getFluidTank().getCapacity()) * 0.625f;

        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        // 옹기 내부 공간에 맞춰 액체 표면 렌더링 (약 3~13 범위)
        float start = 3 / 16f;
        float end = 13 / 16f;

        builder.vertex(matrix, start, fluidHeight, start).color(r, g, b, a).uv(minU, minV).uv2(combinedLight).normal(0, 1, 0);
        builder.vertex(matrix, start, fluidHeight, end).color(r, g, b, a).uv(minU, maxV).uv2(combinedLight).normal(0, 1, 0);
        builder.vertex(matrix, end, fluidHeight, end).color(r, g, b, a).uv(maxU, maxV).uv2(combinedLight).normal(0, 1, 0);
        builder.vertex(matrix, end, fluidHeight, start).color(r, g, b, a).uv(maxU, minV).uv2(combinedLight).normal(0, 1, 0);

        poseStack.popPose();
    }
}
