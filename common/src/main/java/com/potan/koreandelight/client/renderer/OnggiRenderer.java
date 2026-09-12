package com.potan.koreandelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import com.potan.koreandelight.block.custom.OnggiBlock;
import com.potan.koreandelight.fluid.ModFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.joml.Matrix4f;

import java.util.function.Function;

/**
 * 옹기 블록 엔티티의 렌더러입니다.
 * 뚜껑이 열려 있을 때 옹기 내부의 아이템 및 액체 표면을 렌더링합니다.
 */
public class OnggiRenderer implements BlockEntityRenderer<OnggiBlockEntity> {
    private static final ResourceLocation WATER_STILL = ResourceLocation.withDefaultNamespace("block/water_still");
    private static final ResourceLocation SOY_SAUCE_STILL = ResourceLocation.fromNamespaceAndPath(Koreandelight.MODID, "block/soy_sauce_still");

    private final ItemRenderer itemRenderer;

    // 6개 슬롯 아이템 분산 배치 오프셋 (X, Z)
    private static final float[][] ITEM_OFFSETS = {
            {-0.13F, -0.13F},
            {0.0F, -0.16F},
            {0.13F, -0.13F},
            {-0.13F, 0.13F},
            {0.0F, 0.16F},
            {0.13F, 0.13F}
    };

    public OnggiRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(OnggiBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BlockState state = blockEntity.getBlockState();

        // 뚜껑이 닫혀 있을 때는 내부 렌더링을 완전히 생략합니다.
        if (state.hasProperty(OnggiBlock.HAS_LID) && state.getValue(OnggiBlock.HAS_LID)) {
            return;
        }

        Fluid storedFluid = blockEntity.getStoredFluid();
        int fluidAmount = blockEntity.getStoredFluidAmount();
        float fluidY = 0.08F; // 기본 바닥 높이

        // 1. 유체(액체) 표면 렌더링
        if (storedFluid != Fluids.EMPTY && fluidAmount > 0) {
            float minFluidY = 0.10F;
            float maxFluidY = 0.82F;
            fluidY = minFluidY + (fluidAmount / 1000.0F) * (maxFluidY - minFluidY);

            renderFluidSurface(storedFluid, fluidY, poseStack, bufferSource, combinedLight, combinedOverlay);
        }

        // 2. 아이템 3D 렌더링
        float itemBaseY = (storedFluid != Fluids.EMPTY && fluidAmount > 0) ? (fluidY + 0.02F) : 0.08F;

        // 결과물 슬롯(6)에 아이템이 있으면 중앙에 크게 렌더링
        ItemStack outputStack = blockEntity.getInventory().getItem(OnggiBlockEntity.OUTPUT_SLOT);
        if (!outputStack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5F, itemBaseY + 0.05F, 0.5F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            this.itemRenderer.renderStatic(outputStack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        } else {
            // 재료 슬롯(0..5)의 아이템들을 옹기 내부 공간에 자연스럽게 분산 렌더링
            for (int i = 0; i < OnggiBlockEntity.INGREDIENT_SLOTS; i++) {
                ItemStack ingredientStack = blockEntity.getInventory().getItem(i);
                if (!ingredientStack.isEmpty()) {
                    float ox = ITEM_OFFSETS[i][0];
                    float oz = ITEM_OFFSETS[i][1];
                    float rot = (i * 57.0F + (blockEntity.getBlockPos().getX() * 31 + blockEntity.getBlockPos().getZ() * 17)) % 360.0F;

                    poseStack.pushPose();
                    poseStack.translate(0.5F + ox, itemBaseY + 0.02F, 0.5F + oz);
                    poseStack.scale(0.32F, 0.32F, 0.32F);
                    poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(rot));
                    this.itemRenderer.renderStatic(ingredientStack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, blockEntity.getLevel(), i);
                    poseStack.popPose();
                }
            }
        }
    }

    private void renderFluidSurface(Fluid fluid, float yLevel, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        ResourceLocation textureLocation = WATER_STILL;
        float r = 0.25F, g = 0.46F, b = 0.89F, a = 0.80F; // 기본 물 색상

        if (fluid.isSame(ModFluids.SOURCE_SOY_SAUCE.get()) || fluid.isSame(ModFluids.FLOWING_SOY_SAUCE.get())) {
            textureLocation = SOY_SAUCE_STILL;
            r = 1.0F;
            g = 1.0F;
            b = 1.0F;
            a = 0.95F;
        } else if (fluid.isSame(Fluids.WATER)) {
            textureLocation = WATER_STILL;
            r = 0.25F;
            g = 0.46F;
            b = 0.89F;
            a = 0.80F;
        }

        TextureAtlasSprite sprite = atlas.apply(textureLocation);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        float x0 = 0.26F;
        float x1 = 0.74F;
        float z0 = 0.26F;
        float z1 = 0.74F;

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        // 윗면 (Top)
        consumer.addVertex(matrix, x0, yLevel, z0).setColor(r, g, b, a).setUv(u0, v0).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 1, 0);
        consumer.addVertex(matrix, x0, yLevel, z1).setColor(r, g, b, a).setUv(u0, v1).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 1, 0);
        consumer.addVertex(matrix, x1, yLevel, z1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 1, 0);
        consumer.addVertex(matrix, x1, yLevel, z0).setColor(r, g, b, a).setUv(u1, v0).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, 1, 0);

        // 아랫면 (Bottom)
        consumer.addVertex(matrix, x1, yLevel, z0).setColor(r, g, b, a).setUv(u1, v0).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, -1, 0);
        consumer.addVertex(matrix, x1, yLevel, z1).setColor(r, g, b, a).setUv(u1, v1).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, -1, 0);
        consumer.addVertex(matrix, x0, yLevel, z1).setColor(r, g, b, a).setUv(u0, v1).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, -1, 0);
        consumer.addVertex(matrix, x0, yLevel, z0).setColor(r, g, b, a).setUv(u0, v0).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(0, -1, 0);
    }
}
