package com.potan.koreandelight.block.blockentity;

import net.minecraft.SharedConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MortarInteractionTest {
    public static void main(String[] args) {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        var registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        var client = NonNullList.withSize(1, ItemStack.EMPTY);
        client.set(0, new ItemStack(Items.SUGAR));
        MortarBlockEntity.loadItems(new CompoundTag(), client, registries);
        assert client.get(0).isEmpty() : "Empty update must clear the previous item";

        var saved = NonNullList.withSize(1, ItemStack.EMPTY);
        saved.set(0, new ItemStack(Items.WHEAT, 3));
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, saved, registries);
        MortarBlockEntity.loadItems(tag, client, registries);
        assert client.get(0).is(Items.WHEAT) && client.get(0).getCount() == 3;
        MortarBlockEntity.loadItems(new CompoundTag(), client, registries);
        assert client.get(0).isEmpty() : "Taking the item must clear the rendered stack";
        System.out.println("Mortar interaction checks passed");
    }
}
