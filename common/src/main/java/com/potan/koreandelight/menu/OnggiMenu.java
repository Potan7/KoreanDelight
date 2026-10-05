package com.potan.koreandelight.menu;

import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import com.potan.koreandelight.block.custom.OnggiBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class OnggiMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public OnggiMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(OnggiBlockEntity.TOTAL_SLOTS),
                new SimpleContainerData(6), ContainerLevelAccess.NULL, null);
    }

    public OnggiMenu(int id, Inventory playerInventory, OnggiBlockEntity onggi) {
        this(id, playerInventory, onggi.getInventory(), onggi.menuData,
                ContainerLevelAccess.create(onggi.getLevel(), onggi.getBlockPos()), onggi);
    }

    private OnggiMenu(int id, Inventory playerInventory, SimpleContainer inventory, ContainerData data,
                      ContainerLevelAccess access, OnggiBlockEntity onggi) {
        super(ModMenus.ONGGI.get(), id);
        this.data = data;
        this.access = access;
        checkContainerSize(inventory, OnggiBlockEntity.TOTAL_SLOTS);
        checkContainerDataCount(data, 6);

        for (int i = 0; i < OnggiBlockEntity.INGREDIENT_SLOTS; i++) {
            addSlot(new Slot(inventory, i, 18 + (i % 2) * 18, 42 + (i / 2) * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return isLidOpen() && !OnggiBlockEntity.isFluidContainer(stack);
                }

                @Override
                public boolean mayPickup(Player player) {
                    return isLidOpen();
                }

                @Override
                public int getMaxStackSize() {
                    return OnggiBlockEntity.MAX_INGREDIENT_STACK;
                }

                @Override
                public void setChanged() {
                    super.setChanged();
                    if (onggi != null) {
                        onggi.ingredientsChanged();
                    }
                }
            });
        }
        addSlot(new Slot(inventory, OnggiBlockEntity.BUCKET_IN_SLOT, 126, 42) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isLidOpen() && OnggiBlockEntity.isFluidContainer(stack);
            }

            @Override
            public boolean mayPickup(Player player) {
                return isLidOpen();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addSlot(new Slot(inventory, OnggiBlockEntity.BUCKET_OUT_SLOT, 126, 74) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                return isLidOpen();
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 148 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + column * 18, 206));
        }
        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.ONGGI_BLOCK.get())
                && access.evaluate((level, pos) -> !level.getBlockState(pos).getValue(OnggiBlock.HAS_LID), true);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (!isLidOpen() || index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }
        Slot slot = slots.get(index);
        if (!slot.hasItem() || !slot.mayPickup(player)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int playerStart = OnggiBlockEntity.TOTAL_SLOTS;
        if (index < playerStart) {
            if (!moveItemStackTo(stack, playerStart, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (OnggiBlockEntity.isFluidContainer(stack)) {
            if (!moveItemStackTo(stack, OnggiBlockEntity.BUCKET_IN_SLOT, OnggiBlockEntity.BUCKET_IN_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, OnggiBlockEntity.INGREDIENT_SLOTS, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        slot.onTake(player, stack);
        return original;
    }

    public boolean isLidOpen() {
        return data.get(4) == 0;
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    public Fluid getFluid() {
        return BuiltInRegistries.FLUID.byId(data.get(2));
    }

    public int getFluidAmount() {
        return data.get(3);
    }

    public OnggiBlockEntity.RecipeStatus getRecipeStatus() {
        return OnggiBlockEntity.RecipeStatus.values()[data.get(5)];
    }

}
