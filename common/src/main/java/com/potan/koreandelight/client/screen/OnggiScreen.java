package com.potan.koreandelight.client.screen;

import com.potan.koreandelight.menu.OnggiMenu;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import com.potan.koreandelight.fluid.ModFluids;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class OnggiScreen extends AbstractContainerScreen<OnggiMenu> {
    public OnggiScreen(OnggiMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageHeight = 230;
        inventoryLabelY = 136;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF37312D);
        graphics.fill(x + 1, y + 1, x + imageWidth - 1, y + imageHeight - 1, 0xFFF6EEE2);
        graphics.fill(x + 3, y + 3, x + imageWidth - 3, y + imageHeight - 3, 0xFFDED2BE);
        graphics.fill(x + 4, y + 21, x + imageWidth - 4, y + 23, 0xFF9A8064);
        for (Slot slot : menu.slots) {
            int sx = x + slot.x - 1;
            int sy = y + slot.y - 1;
            graphics.fill(sx, sy, sx + 18, sy + 18, 0xFF645747);
            graphics.fill(sx + 1, sy + 1, sx + 18, sy + 18, 0xFFFFF4DF);
            graphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFFA99B88);
        }
        // Progress and fluid are drawn with the game's GUI primitives.
        graphics.fill(x + 18, y + 83, x + 90, y + 91, 0xFF645747);
        graphics.fill(x + 19, y + 84, x + 89, y + 90, 0xFFBBAE9B);
        if (menu.getMaxProgress() > 0) {
            int width = Math.min(70, 70 * menu.getProgress() / menu.getMaxProgress());
            graphics.fill(x + 19, y + 84, x + 19 + width, y + 90, 0xFF77904D);
        }
        graphics.fill(x + 148, y + 41, x + 162, y + 91, 0xFF645747);
        graphics.fill(x + 149, y + 42, x + 161, y + 90, 0xFFBBAE9B);
        int fluidHeight = Math.clamp(48 * menu.getFluidAmount() / OnggiBlockEntity.TANK_CAPACITY, 0, 48);
        int color = menu.getFluid().isSame(Fluids.WATER) ? 0xFF477BB8
                : menu.getFluid().isSame(Fluids.LAVA) ? 0xFFFF771F
                : 0xFF61402B;
        graphics.fill(x + 149, y + 90 - fluidHeight, x + 161, y + 90, color);
        if (!menu.isLidOpen()) {
            for (int i = 0; i < OnggiBlockEntity.TOTAL_SLOTS; i++) {
                Slot slot = menu.slots.get(i);
                graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0x38645342);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        Component status;
        var recipeStatus = menu.getRecipeStatus();
        int color = recipeStatus == OnggiBlockEntity.RecipeStatus.READY ? 0x28752B : 0xB32B25;
        if (!menu.isLidOpen() && menu.getMaxProgress() > 0) {
            status = Component.translatable("gui.koreandelight.onggi.fermenting",
                    100 * menu.getProgress() / menu.getMaxProgress());
        } else {
            status = Component.translatable("gui.koreandelight.onggi.status."
                    + recipeStatus.name().toLowerCase(Locale.ROOT));
        }
        graphics.drawWordWrap(font, status, 8, 98, imageWidth - 16, color);
        graphics.drawString(font, Component.translatable("gui.koreandelight.onggi.lid_hint"), 8, 124, 0x645747, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (isHovering(148, 41, 14, 50, mouseX, mouseY)) {
            Component fluid = menu.getFluidAmount() == 0
                    ? Component.translatable("gui.koreandelight.onggi.fluid.empty")
                    : menu.getFluid().isSame(Fluids.WATER)
                    ? Component.translatable("gui.koreandelight.onggi.fluid.water")
                    : menu.getFluid().getFluidType().getDescription();
            graphics.renderComponentTooltip(font, List.of(
                    fluid,
                    Component.literal(menu.getFluidAmount() + " / " + OnggiBlockEntity.TANK_CAPACITY + " mB")), mouseX, mouseY);
        } else if (isHovering(18, 83, 72, 8, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.koreandelight.onggi.environment",
                    Component.translatable(menu.getEnvironment().getTranslationKey())));
            tooltip.add(Component.translatable("gui.koreandelight.onggi.speed", menu.getProgressPerTick()));
            if (menu.getMaxProgress() > 0) {
                int seconds = Math.max(0, (int) Math.ceil((menu.getMaxProgress() - menu.getProgress())
                        / (20.0 * menu.getProgressPerTick())));
                tooltip.add(Component.translatable("gui.koreandelight.onggi.remaining", seconds));
            }
            graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        } else if (hoveredSlot != null && !hoveredSlot.hasItem()
                && hoveredSlot.index >= OnggiBlockEntity.BUCKET_IN_SLOT && hoveredSlot.index <= OnggiBlockEntity.BUCKET_OUT_SLOT) {
            graphics.renderTooltip(font, Component.translatable(hoveredSlot.index == OnggiBlockEntity.BUCKET_IN_SLOT
                    ? "gui.koreandelight.onggi.bucket_in" : "gui.koreandelight.onggi.bucket_out"), mouseX, mouseY);
        }
    }
}
