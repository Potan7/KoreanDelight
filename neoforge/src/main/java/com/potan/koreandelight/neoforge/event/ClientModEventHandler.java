package com.potan.koreandelight.neoforge.event;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.fluid.BaseFluidType;
import com.potan.koreandelight.fluid.ModFluidTypes;
import com.potan.koreandelight.item.ModItems;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * NeoForge 클라이언트 전용 모드 버스 이벤트 핸들러입니다.
 * 물리적 클라이언트(Dist.CLIENT) 환경에서만 로드되어 서버에서의 클래스로딩 에러를 방지합니다.
 */
@EventBusSubscriber(modid = Koreandelight.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventHandler {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(new DynamicFluidContainerModel.Colors(), ModItems.SOY_SAUCE_BUCKET.get());
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        if (ModFluidTypes.SOY_SAUCE_FLUID_TYPE.get() instanceof BaseFluidType baseFluidType) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return baseFluidType.getStillTexture();
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return baseFluidType.getFlowingTexture();
                }

                @Override
                public @Nullable ResourceLocation getOverlayTexture() {
                    return baseFluidType.getOverlayTexture();
                }

                @Override
                public int getTintColor() {
                    return baseFluidType.getTintColor();
                }

                @Override
                public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                    return baseFluidType.getFogColor();
                }

                @Override
                public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                    RenderSystem.setShaderFogStart(1.0F);
                    RenderSystem.setShaderFogEnd(6.0F);
                }
            }, baseFluidType);
        }
    }
}
