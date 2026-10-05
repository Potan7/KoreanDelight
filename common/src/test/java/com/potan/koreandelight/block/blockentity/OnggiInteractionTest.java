package com.potan.koreandelight.block.blockentity;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import com.mojang.serialization.JsonOps;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import com.potan.koreandelight.recipe.FermentationRecipeSerializer;
import com.potan.koreandelight.recipe.FermentationRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;

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
        inventory.setItem(1, new ItemStack(Items.STONE, 7));
        assert OnggiBlockEntity.insertItem(inventory, held);
        assert inventory.getItem(0).isEmpty();
        assert inventory.getItem(1).getCount() == 8;
        assert OnggiBlockEntity.insertItem(inventory, held);
        assert inventory.getItem(0).getCount() == 1;

        for (int i = 0; i < OnggiBlockEntity.INGREDIENT_SLOTS; i++) {
            inventory.setItem(i, new ItemStack(Items.STONE, 8));
        }
        assert !OnggiBlockEntity.insertItem(inventory, held);
        assert !OnggiBlockEntity.insertItem(inventory, new ItemStack(Items.DIRT));
        testFermentedContents();
        testBucketTransfers();
        testRecipeStatus();
        testBottleTransfers();
        testFluidOnlyRecipe();
        System.out.println("Onggi interaction checks passed");
    }

    private static void testFermentedContents() {
        SimpleContainer inventory = new SimpleContainer(OnggiBlockEntity.TOTAL_SLOTS);
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(Items.STONE));
        ingredients.add(Ingredient.of(Items.DIRT));
        FermentationRecipe recipe = new FermentationRecipe(new ItemStack(Items.BREAD, 4), ingredients,
                Optional.empty(), Optional.empty(), 200);
        inventory.setItem(1, new ItemStack(Items.DIRT, 2));
        inventory.setItem(3, new ItemStack(Items.STONE, 2));
        inventory.setItem(OnggiBlockEntity.BUCKET_OUT_SLOT, new ItemStack(Items.BUCKET));
        var transformed = OnggiBlockEntity.createFermentedContents(recipe, inventory, new ItemStack(Items.BREAD, 4));
        assert transformed != null && transformed.countItem(Items.BREAD) == 8;
        assert transformed.countItem(Items.STONE) == 0 && transformed.countItem(Items.DIRT) == 0;
        assert inventory.getItem(1).getCount() == 2 && inventory.getItem(3).getCount() == 2;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.BUCKET);
        inventory.setItem(3, new ItemStack(Items.STONE, 3));
        assert OnggiBlockEntity.createFermentedContents(recipe, inventory, new ItemStack(Items.BREAD)) == null;

        // Four repeated ingredients can be stacked or spread across the jar.
        inventory.clearContent();
        ingredients.clear();
        for (int i = 0; i < 4; i++) {
            ingredients.add(Ingredient.of(Items.STONE));
            inventory.setItem(i, new ItemStack(Items.STONE, 2));
        }
        transformed = OnggiBlockEntity.createFermentedContents(recipe, inventory, new ItemStack(Items.BREAD));
        assert transformed != null && transformed.countItem(Items.BREAD) == 2;
        inventory.clearContent();
        inventory.setItem(0, new ItemStack(Items.STONE, 8));
        transformed = OnggiBlockEntity.createFermentedContents(recipe, inventory, new ItemStack(Items.BREAD));
        assert transformed != null && transformed.countItem(Items.BREAD) == 2;

        ingredients.clear();
        ingredients.add(Ingredient.of(Items.STONE));
        inventory.setItem(0, new ItemStack(Items.STONE, 8));
        transformed = OnggiBlockEntity.createFermentedContents(recipe, inventory, new ItemStack(Items.BREAD, 4));
        assert transformed != null && transformed.countItem(Items.BREAD) == 32;
        transformed = OnggiBlockEntity.createFermentedContents(recipe, inventory, new ItemStack(Items.BREAD, 20));
        assert transformed != null && transformed.countItem(Items.BREAD) == 160;
        assert transformed.getItem(0).getCount() == 64 && transformed.getItem(2).getCount() == 32;
        assert OnggiBlockEntity.createFermentedContents(recipe, inventory, new ItemStack(Items.BREAD, 64)) == null;
        inventory.setItem(1, new ItemStack(Items.STONE));
        assert OnggiBlockEntity.createFermentedContents(recipe, inventory, new ItemStack(Items.BREAD)).countItem(Items.BREAD) == 9;
        inventory.setItem(1, new ItemStack(Items.DIRT));
        assert OnggiBlockEntity.createFermentedContents(recipe, inventory, new ItemStack(Items.BREAD)) == null;

        // Overlapping ingredients still match, and finished contents can age further.
        inventory.clearContent();
        ingredients.clear();
        ingredients.add(Ingredient.of(Items.STONE, Items.DIRT));
        ingredients.add(Ingredient.of(Items.STONE));
        inventory.setItem(0, new ItemStack(Items.STONE, 3));
        inventory.setItem(1, new ItemStack(Items.DIRT, 3));
        transformed = OnggiBlockEntity.createFermentedContents(recipe, inventory, new ItemStack(Items.BREAD));
        assert transformed != null && transformed.countItem(Items.BREAD) == 3;
        FermentationRecipe aging = new FermentationRecipe(new ItemStack(Items.COOKIE), Ingredient.of(Items.BREAD),
                Optional.empty(), Optional.empty(), 100);
        transformed = OnggiBlockEntity.createFermentedContents(aging, transformed, new ItemStack(Items.COOKIE));
        assert transformed != null && transformed.countItem(Items.COOKIE) == 3;

        // The entire liquid contents must match the batch ratio too.
        FermentationRecipe fluidRecipe = new FermentationRecipe(new ItemStack(Items.BREAD, 4), Ingredient.of(Items.STONE),
                Optional.of(new com.potan.koreandelight.recipe.FluidStackData(Fluids.WATER, 1000)),
                Optional.of(new com.potan.koreandelight.recipe.FluidStackData(Fluids.LAVA, 1000)), 200);
        assert OnggiBlockEntity.createFermentedFluid(fluidRecipe, Fluids.WATER, 2000, 8) == null;
        assert OnggiBlockEntity.createFermentedFluid(fluidRecipe, Fluids.WATER, 8000, 2) == null;
        assert OnggiBlockEntity.createFermentedFluid(fluidRecipe, Fluids.LAVA, 8000, 8) == null;
        var fluidResult = OnggiBlockEntity.createFermentedFluid(fluidRecipe, Fluids.WATER, 8000, 8);
        assert fluidResult != null && fluidResult.fluid() == Fluids.LAVA && fluidResult.amount() == 8000;
        assert OnggiBlockEntity.createFermentedFluid(aging, Fluids.WATER, 1000, 1) == null;
        assert OnggiBlockEntity.createFermentedFluid(aging, Fluids.EMPTY, 0, 3).amount() == 0;
        assert OnggiBlockEntity.getBatchTime(200, 1) == 200;
        assert OnggiBlockEntity.getBatchTime(200, 2) == 350;
        assert OnggiBlockEntity.getBatchTime(200, 3) == 500;
        assert OnggiBlockEntity.getBatchTime(200, 8) == 1250;
        assert OnggiBlockEntity.getBatchTime(1, 2) == 2;
    }
    private static void testRecipeStatus() {
        SimpleContainer inventory = new SimpleContainer(OnggiBlockEntity.TOTAL_SLOTS);
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(Items.STONE));
        ingredients.add(Ingredient.of(Items.DIRT));
        ItemStack result = new ItemStack(Items.BREAD);
        FermentationRecipe recipe = new FermentationRecipe(result, ingredients,
                Optional.of(new com.potan.koreandelight.recipe.FluidStackData(Fluids.WATER, 1000)),
                Optional.empty(), 200);
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.EMPTY, 0, result)
                == OnggiBlockEntity.RecipeStatus.EMPTY;
        inventory.setItem(0, new ItemStack(Items.STONE, 2));
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.WATER, 2000, result)
                == OnggiBlockEntity.RecipeStatus.MISSING_INGREDIENTS;
        inventory.setItem(1, new ItemStack(Items.DIRT));
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.WATER, 2000, result)
                == OnggiBlockEntity.RecipeStatus.WRONG_RATIO;
        inventory.setItem(1, new ItemStack(Items.DIRT, 2));
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.WATER, 2000, result)
                == OnggiBlockEntity.RecipeStatus.READY;
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.WATER, 1000, result)
                == OnggiBlockEntity.RecipeStatus.TOO_LITTLE_FLUID;
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.EMPTY, 0, result)
                == OnggiBlockEntity.RecipeStatus.TOO_LITTLE_FLUID;
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.WATER, 3000, result)
                == OnggiBlockEntity.RecipeStatus.TOO_MUCH_FLUID;
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.LAVA, 2000, result)
                == OnggiBlockEntity.RecipeStatus.WRONG_FLUID;
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.WATER, 2000, new ItemStack(Items.BREAD, 200))
                == OnggiBlockEntity.RecipeStatus.OUTPUT_FULL;
        inventory.setItem(2, new ItemStack(Items.COAL));
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.WATER, 2000, result)
                == OnggiBlockEntity.RecipeStatus.NO_RECIPE;
        inventory.setItem(2, ItemStack.EMPTY);
        recipe = new FermentationRecipe(result, ingredients, Optional.empty(), Optional.empty(), 200);
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.WATER, 2000, result)
                == OnggiBlockEntity.RecipeStatus.UNEXPECTED_FLUID;
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.EMPTY, 0, result)
                == OnggiBlockEntity.RecipeStatus.READY;
        inventory.setItem(0, new ItemStack(Items.STONE, 9));
        inventory.setItem(1, new ItemStack(Items.DIRT, 9));
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.EMPTY, 0, result)
                == OnggiBlockEntity.RecipeStatus.READY;
        assert inventory.getItem(0).getCount() == 9 && inventory.getItem(1).getCount() == 9;
        recipe = new FermentationRecipe(result, Ingredient.of(Items.STONE), Optional.empty(), Optional.empty(), 200);
        for (int i = 0; i < OnggiBlockEntity.INGREDIENT_SLOTS; i++) {
            inventory.setItem(i, new ItemStack(Items.STONE, 8));
        }
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.EMPTY, 0, result)
                == OnggiBlockEntity.RecipeStatus.READY;
        assert OnggiBlockEntity.createFermentedContents(recipe, inventory, result).countItem(Items.BREAD) == 32;
        recipe = new FermentationRecipe(result, Ingredient.of(Items.STONE),
                Optional.of(new com.potan.koreandelight.recipe.FluidStackData(Fluids.WATER, 1000)), Optional.empty(), 200);
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.WATER, 8000, result)
                == OnggiBlockEntity.RecipeStatus.TOO_LITTLE_FLUID;
    }

    private static void testFluidOnlyRecipe() {
        var serializer = new FermentationRecipeSerializer();
        var json = JsonParser.parseString("""
                {"ingredient":{"item":"minecraft:stone"},
                 "fluid":{"fluid":"minecraft:water","amount":1000},
                 "result_fluid":{"fluid":"minecraft:lava","amount":1000}}
                """);
        var recipe = serializer.codec().codec().parse(JsonOps.INSTANCE, json).getOrThrow();
        assert recipe.getOutput().isEmpty() && recipe.getResultFluid().get().amount() == 1000;
        var encoded = serializer.codec().codec().encodeStart(JsonOps.INSTANCE, recipe).getOrThrow().getAsJsonObject();
        assert !encoded.has("result") && encoded.has("result_fluid");
        assert serializer.codec().codec().parse(JsonOps.INSTANCE,
                JsonParser.parseString("{\"ingredient\":{\"item\":\"minecraft:stone\"}}")).error().isPresent();
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        // The standalone bootstrap has no synced item registry; isolate the empty-result payload.
        var fluidPayload = new FermentationRecipe(ItemStack.EMPTY, Ingredient.EMPTY,
                recipe.getFluid(), recipe.getResultFluid(), recipe.getFermentationTime());
        serializer.streamCodec().encode(buffer, fluidPayload);
        var decoded = serializer.streamCodec().decode(buffer);
        assert decoded.getOutput().isEmpty() && decoded.getResultFluid().get().fluid() == Fluids.LAVA;
        buffer.release();
        SimpleContainer inventory = new SimpleContainer(OnggiBlockEntity.TOTAL_SLOTS);
        inventory.setItem(0, new ItemStack(Items.STONE, 2));
        var contents = OnggiBlockEntity.createFermentedContents(recipe, inventory, ItemStack.EMPTY);
        assert contents != null && contents.isEmpty();
        assert inventory.getItem(0).getCount() == 2;
        assert OnggiBlockEntity.checkRecipeStatus(recipe, inventory, Fluids.WATER, 2000, ItemStack.EMPTY)
                == OnggiBlockEntity.RecipeStatus.READY;
        assert OnggiBlockEntity.createFermentedFluid(recipe, Fluids.WATER, 2000, 2).amount() == 2000;
    }

    private static void testBottleTransfers() {
        SimpleContainer inventory = new SimpleContainer(OnggiBlockEntity.TOTAL_SLOTS);
        ItemStack water = PotionContents.createItemStack(Items.POTION, Potions.WATER);
        assert OnggiBlockEntity.isFluidContainer(water);
        assert OnggiBlockEntity.isFluidContainer(new ItemStack(Items.GLASS_BOTTLE));
        assert !OnggiBlockEntity.isFluidContainer(PotionContents.createItemStack(Items.POTION, Potions.HEALING));
        for (int i = 0; i < 4; i++) {
            inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, water.copy());
            var filled = OnggiBlockEntity.transferFluidContainer(inventory, i == 0 ? Fluids.EMPTY : Fluids.WATER, i * 250);
            assert filled != null && filled.fluid() == Fluids.WATER && filled.amount() == (i + 1) * 250;
        }
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.GLASS_BOTTLE);
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).getCount() == 4;
        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.BUCKET));
        assert OnggiBlockEntity.transferFluidContainer(inventory, Fluids.WATER, 1000).amount() == 0;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.WATER_BUCKET);

        // Bottle extraction returns exactly 250 mB, including the last quarter bucket.
        for (int amount : new int[]{250, 1000, 8000}) {
            inventory.clearContent();
            inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.GLASS_BOTTLE));
            var drained = OnggiBlockEntity.transferFluidContainer(inventory, Fluids.WATER, amount);
            assert drained.amount() == amount - 250;
            assert drained.fluid() == (amount == 250 ? Fluids.EMPTY : Fluids.WATER);
            assert ItemStack.isSameItemSameComponents(inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT), water);
        }
        for (int amount : new int[]{0, 200}) {
            inventory.clearContent();
            inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.GLASS_BOTTLE));
            assert OnggiBlockEntity.transferFluidContainer(inventory, Fluids.WATER, amount).amount() == amount;
            assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.GLASS_BOTTLE);
        }
        for (int amount : new int[]{7900, 8000}) {
            inventory.clearContent();
            inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, water.copy());
            assert OnggiBlockEntity.transferFluidContainer(inventory, Fluids.WATER, amount).amount() == amount;
            assert ItemStack.isSameItemSameComponents(inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT), water);
        }
        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, water.copy());
        inventory.setItem(OnggiBlockEntity.BUCKET_OUT_SLOT, new ItemStack(Items.GLASS_BOTTLE, 64));
        assert OnggiBlockEntity.transferFluidContainer(inventory, Fluids.WATER, 1000) == null;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_IN_SLOT).getCount() == 1;
        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, water.copy());
        assert OnggiBlockEntity.transferFluidContainer(inventory, Fluids.LAVA, 1000).fluid() == Fluids.LAVA;
        assert ItemStack.isSameItemSameComponents(inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT), water);
        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.GLASS_BOTTLE));
        assert OnggiBlockEntity.transferFluidContainer(inventory, Fluids.LAVA, 1000).amount() == 1000;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.GLASS_BOTTLE);
    }

    private static void testBucketTransfers() {
        SimpleContainer inventory = new SimpleContainer(OnggiBlockEntity.TOTAL_SLOTS);
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.WATER_BUCKET));
        var filled = OnggiBlockEntity.transferFluidContainer(inventory, Fluids.EMPTY, 0);
        assert filled != null && filled.fluid() == Fluids.WATER && filled.amount() == 1000;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_IN_SLOT).isEmpty();
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.BUCKET);

        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.BUCKET));
        var drained = OnggiBlockEntity.transferFluidContainer(inventory, Fluids.WATER, 1000);
        assert drained != null && drained.fluid() == Fluids.EMPTY && drained.amount() == 0;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.WATER_BUCKET);

        // Eight buckets fit; additions and removals transfer exactly one bucket at a time.
        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.WATER_BUCKET));
        filled = OnggiBlockEntity.transferFluidContainer(inventory, Fluids.WATER, 7000);
        assert filled != null && filled.amount() == 8000;
        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.WATER_BUCKET));
        var overflowing = OnggiBlockEntity.transferFluidContainer(inventory, Fluids.WATER, 8000);
        assert overflowing != null && overflowing.amount() == 8000;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.WATER_BUCKET);
        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.BUCKET));
        drained = OnggiBlockEntity.transferFluidContainer(inventory, Fluids.WATER, 8000);
        assert drained != null && drained.fluid() == Fluids.WATER && drained.amount() == 7000;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.WATER_BUCKET);

        // Empty tanks return empty buckets, including multiple queued buckets.
        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.BUCKET, 2));
        inventory.setItem(OnggiBlockEntity.BUCKET_OUT_SLOT, new ItemStack(Items.BUCKET, 15));
        var empty = OnggiBlockEntity.transferFluidContainer(inventory, Fluids.EMPTY, 0);
        assert empty != null && empty.amount() == 0;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).getCount() == 16;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_IN_SLOT).getCount() == 1;
        assert OnggiBlockEntity.transferFluidContainer(inventory, Fluids.EMPTY, 0) == null;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_IN_SLOT).getCount() == 1;

        // A full tank returns a filled input unchanged rather than overwriting fluid.
        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.WATER_BUCKET));
        var full = OnggiBlockEntity.transferFluidContainer(inventory, Fluids.LAVA, 1000);
        assert full != null && full.fluid() == Fluids.LAVA && full.amount() == 1000;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.WATER_BUCKET);

        // Output blockage must not consume an input or alter the tank.
        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.BUCKET));
        inventory.setItem(OnggiBlockEntity.BUCKET_OUT_SLOT, new ItemStack(Items.LAVA_BUCKET));
        assert OnggiBlockEntity.transferFluidContainer(inventory, Fluids.WATER, 1000) == null;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_IN_SLOT).is(Items.BUCKET);
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.LAVA_BUCKET);

        inventory.clearContent();
        inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, new ItemStack(Items.LAVA_BUCKET));
        var lava = OnggiBlockEntity.transferFluidContainer(inventory, Fluids.EMPTY, 0);
        assert lava != null && lava.fluid() == Fluids.LAVA && lava.amount() == 1000;
        assert inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT).is(Items.BUCKET);

        // Mob, milk and snow buckets keep their contents on passage.
        for (var item : new net.minecraft.world.item.Item[]{Items.COD_BUCKET, Items.MILK_BUCKET, Items.POWDER_SNOW_BUCKET}) {
            inventory.clearContent();
            ItemStack input = new ItemStack(item);
            assert OnggiBlockEntity.isFluidContainer(input);
            inventory.setItem(OnggiBlockEntity.BUCKET_IN_SLOT, input.copy());
            var unchanged = OnggiBlockEntity.transferFluidContainer(inventory, Fluids.EMPTY, 0);
            assert unchanged != null && unchanged.fluid() == Fluids.EMPTY && unchanged.amount() == 0;
            assert ItemStack.isSameItemSameComponents(input, inventory.getItem(OnggiBlockEntity.BUCKET_OUT_SLOT));
        }
        assert !OnggiBlockEntity.isFluidContainer(new ItemStack(Items.STONE));
    }
}
