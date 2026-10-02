package com.potan.koreandelight.block.blockentity;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class OnggiInteractionTest {
    public static void main(String[] args) {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        SimpleContainer inventory = new SimpleContainer(OnggiBlockEntity.TOTAL_SLOTS);
        ItemStack held = new ItemStack(Items.STONE, 32);
        assert !OnggiBlockEntity.insertItem(inventory, ItemStack.EMPTY);
        assert OnggiBlockEntity.insertItem(inventory, held);
        assert inventory.getItem(0).getCount() == 1;
        assert held.getCount() == 32;

        // Merge before using an earlier empty slot.
        inventory.setItem(0, ItemStack.EMPTY);
        inventory.setItem(1, new ItemStack(Items.STONE, 63));
        assert OnggiBlockEntity.insertItem(inventory, held);
        assert inventory.getItem(0).isEmpty();
        assert inventory.getItem(1).getCount() == 64;
        assert OnggiBlockEntity.insertItem(inventory, held);
        assert inventory.getItem(0).getCount() == 1;

        for (int i = 0; i < OnggiBlockEntity.INGREDIENT_SLOTS; i++) {
            inventory.setItem(i, new ItemStack(Items.STONE, 64));
        }
        assert !OnggiBlockEntity.insertItem(inventory, held);
        assert !OnggiBlockEntity.insertItem(inventory, new ItemStack(Items.DIRT));
        inventory.setItem(OnggiBlockEntity.OUTPUT_SLOT, new ItemStack(Items.BREAD, 3));
        ItemStack result = OnggiBlockEntity.extractItem(inventory);
        assert result.is(Items.BREAD) && result.getCount() == 3;
        assert inventory.getItem(OnggiBlockEntity.OUTPUT_SLOT).isEmpty();
        assert inventory.getItem(5).getCount() == 64;
        assert OnggiBlockEntity.extractItem(inventory).getCount() == 64;
        assert inventory.getItem(5).isEmpty();

        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_OUT_SLOT, new ItemStack(Items.BUCKET));
        assert OnggiBlockEntity.extractItem(inventory).is(Items.BUCKET);
        assert OnggiBlockEntity.extractItem(inventory).isEmpty();
        System.out.println("Onggi interaction checks passed");
    }
}
