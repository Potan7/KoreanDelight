package com.potan.koreandelight.client.gui;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.fluid.ModFluids;
import com.potan.koreandelight.menu.OnggiMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public class OnggiScreen extends AbstractContainerScreen<OnggiMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            Koreandelight.MODID,
            "textures/gui/onggi_gui.png"
    );

    public OnggiScreen(OnggiMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 61;
        this.titleLabelY = 12;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // 1. GUI 배경 텍스처
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // 2. 발효 진행 화살표 렌더링
        int progress = this.menu.getAgingProgressionScaled(22);
        if (progress > 0) {
            guiGraphics.blit(TEXTURE, x + 119, y + 34, 176, 0, progress, 16);
        }

        // 3. 유체 탱크 게이지 바 렌더링
        int fluidAmount = this.menu.getStoredFluidAmount();
        Fluid fluid = this.menu.getStoredFluid();

        if (fluidAmount > 0 && fluid != Fluids.EMPTY) {
            int fluidHeight = Math.min(48, (int) (fluidAmount / 1000.0F * 48));
            int color = 0xCC3F76E4; // 기본 물 색상

            if (fluid.isSame(ModFluids.SOURCE_SOY_SAUCE.get()) || fluid.isSame(ModFluids.FLOWING_SOY_SAUCE.get())) {
                color = 0xF025170F; // 간장 진갈색/흑갈색
            } else if (fluid.isSame(Fluids.WATER)) {
                color = 0xCC3F76E4;
            }

            int startY = y + 20 + (48 - fluidHeight);
            guiGraphics.fill(x + 13, startY, x + 27, y + 68, color);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);

        // 유체 탱크 호버 툴팁 (11, 18, 18x52)
        if (this.isHovering(11, 18, 18, 52, x, y)) {
            List<Component> tooltip = new ArrayList<>();
            Fluid fluid = this.menu.getStoredFluid();
            int amount = this.menu.getStoredFluidAmount();

            if (fluid == Fluids.EMPTY || amount <= 0) {
                tooltip.add(Component.translatable("gui.koreandelight.onggi.fluid_empty").withStyle(ChatFormatting.GRAY));
            } else {
                Component fluidName;
                if (fluid.isSame(Fluids.WATER)) {
                    fluidName = Component.translatable("block.minecraft.water");
                } else if (fluid.isSame(ModFluids.SOURCE_SOY_SAUCE.get()) || fluid.isSame(ModFluids.FLOWING_SOY_SAUCE.get())) {
                    fluidName = Component.translatable("fluid.koreandelight.soy_sauce");
                } else {
                    fluidName = Component.literal(fluid.toString());
                }

                tooltip.add(fluidName);
                tooltip.add(Component.literal(amount + " / 1000 mB").withStyle(ChatFormatting.GRAY));
            }

            guiGraphics.renderComponentTooltip(this.font, tooltip, x, y);
        }
    }
}
