package com.potan.koreandelight.neoforge.integration;

import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RiceBowlServingTest {
    public static void main(String[] args) {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        ItemStack serving = new ItemStack(Items.BREAD);
        serving.set(DataComponents.MAX_STACK_SIZE, 16);

        ItemStack meal = new ItemStack(Items.BREAD, 10);
        ItemStack bowls = new ItemStack(Items.BOWL, 3);
        ItemStack result = RiceBowlServing.fillStoredBowls(meal, bowls, ItemStack.EMPTY, serving);
        assert result.getCount() == 3;
        assert meal.getCount() == 7 && bowls.isEmpty();
        assert serving.getCount() == 1;
        assert RiceBowlServing.fillStoredBowls(meal, bowls, result, serving).isEmpty();
        assert meal.getCount() == 7;

        meal = new ItemStack(Items.BREAD, 2);
        bowls = new ItemStack(Items.BOWL, 10);
        result = RiceBowlServing.fillStoredBowls(meal, bowls, ItemStack.EMPTY, serving);
        assert result.getCount() == 2;
        assert meal.isEmpty() && bowls.getCount() == 8;

        meal = new ItemStack(Items.BREAD, 64);
        bowls = new ItemStack(Items.BOWL, 64);
        ItemStack output = serving.copyWithCount(15);
        result = RiceBowlServing.fillStoredBowls(meal, bowls, output, serving);
        assert result.getCount() == 16;
        assert meal.getCount() == 63 && bowls.getCount() == 63;
        assert output.getCount() == 15;
        assert RiceBowlServing.fillStoredBowls(meal, bowls, result, serving).isEmpty();
        assert meal.getCount() == 63 && bowls.getCount() == 63;

        assert RiceBowlServing.fillStoredBowls(meal, bowls, new ItemStack(Items.STONE), serving).isEmpty();
        assert meal.getCount() == 63 && bowls.getCount() == 63;

        output = serving.copy();
        output.set(DataComponents.CUSTOM_NAME, Component.literal("Reserved serving"));
        assert RiceBowlServing.fillStoredBowls(meal, bowls, output, serving).isEmpty();
        assert meal.getCount() == 63 && bowls.getCount() == 63;
        assert RiceBowlServing.fillStoredBowls(ItemStack.EMPTY, bowls, ItemStack.EMPTY, serving).isEmpty();
        assert bowls.getCount() == 63;
        System.out.println("Rice bowl serving checks passed");
    }

}
