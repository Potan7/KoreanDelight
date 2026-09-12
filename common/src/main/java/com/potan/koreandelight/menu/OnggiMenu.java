package com.potan.koreandelight.menu;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class OnggiMenu extends AbstractContainerMenu {
    public static final int INGREDIENT_SLOTS = 6;
    public static final int OUTPUT_SLOT = 6;
    public static final int BUCKET_IN_SLOT = 7;
    public static final int BUCKET_OUT_SLOT = 8;
    public static final int TOTAL_SLOTS = 9;

    private final Container container;
    private final ContainerData data;

    // Client-side constructor
    public OnggiMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(TOTAL_SLOTS), new SimpleContainerData(4));
    }

    // Server-side constructor
    public OnggiMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuTypes.ONGGI_MENU.get(), containerId);
        checkContainerSize(container, TOTAL_SLOTS);
        checkContainerDataCount(data, 4);

        this.container = container;
        this.data = data;
        container.startOpen(playerInventory.player);

        // 1. 6개 재료 슬롯 (3열 x 2행)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIndex = col + row * 3;
                this.addSlot(new Slot(container, slotIndex, 62 + col * 18, 26 + row * 18));
            }
        }

        // 2. 결과물 슬롯
        this.addSlot(new Slot(container, OUTPUT_SLOT, 148, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // 3. 유체 양동이 입출력 슬롯
        this.addSlot(new Slot(container, BUCKET_IN_SLOT, 35, 19));
        this.addSlot(new Slot(container, BUCKET_OUT_SLOT, 35, 51) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // 4. 플레이어 인벤토리 (3행 x 9열)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // 5. 플레이어 핫바 (1행 x 9열)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();

            // 옹기 내부 슬롯 (0..8)에서 플레이어 인벤토리로 이동
            if (index < TOTAL_SLOTS) {
                if (!this.moveItemStackTo(stackInSlot, TOTAL_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 플레이어 인벤토리에서 옹기 내부로 이동
                // 1) 양동이 류는 양동이 입력 슬롯(7)으로 우선 시도
                if (stackInSlot.getItem() instanceof net.minecraft.world.item.BucketItem ||
                    stackInSlot.is(net.minecraft.world.item.Items.BUCKET)) {
                    if (!this.moveItemStackTo(stackInSlot, BUCKET_IN_SLOT, BUCKET_IN_SLOT + 1, false)) {
                        if (!this.moveItemStackTo(stackInSlot, 0, INGREDIENT_SLOTS, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    // 2) 일반 아이템은 재료 슬롯(0..5)으로 이동 시도
                    if (!this.moveItemStackTo(stackInSlot, 0, INGREDIENT_SLOTS, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public int getAgingProgressionScaled(int pixels) {
        int progress = this.data.get(0);
        int max = this.data.get(1);
        return max != 0 && progress != 0 ? progress * pixels / max : 0;
    }

    public int getAgingProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }

    public int getStoredFluidAmount() {
        return this.data.get(2);
    }

    public Fluid getStoredFluid() {
        int fluidId = this.data.get(3);
        return fluidId > 0 ? BuiltInRegistries.FLUID.byId(fluidId) : Fluids.EMPTY;
    }
}
