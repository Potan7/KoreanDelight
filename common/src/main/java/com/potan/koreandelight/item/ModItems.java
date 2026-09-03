package com.potan.koreandelight.item;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.block.ModCropBlocks;
import com.potan.koreandelight.fluid.ModFluids;
import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public class ModItems {
    public static final RegistrationProvider<Item> ITEMS = Services.PLATFORM.getProvider(Registries.ITEM);

    // 간장 양동이
    public static final Supplier<Item> SOY_SAUCE_BUCKET = ITEMS.register(
            "soy_sauce_bucket",
            () -> new BucketItem(ModFluids.SOURCE_SOY_SAUCE.get(), new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1))
    );

    // 배추 씨앗
    public static final Supplier<Item> KIMCHI_CABBAGE_SEEDS = ITEMS.register(
            "kimchi_cabbage_seeds",
            () -> new ItemNameBlockItem(ModCropBlocks.KIMCHI_CABBAGE_CROP.get(), new Item.Properties())
    );

    // 고추 씨앗
    public static final Supplier<Item> RED_PEPPER_SEEDS = ITEMS.register(
            "red_pepper_seeds",
            () -> new ItemNameBlockItem(ModCropBlocks.RED_PEPPER_CROP.get(), new Item.Properties())
    );

    // 대파 씨앗
    public static final Supplier<Item> GREEN_ONION_SEEDS = ITEMS.register(
            "green_onion_seeds",
            () -> new ItemNameBlockItem(ModCropBlocks.GREEN_ONION_CROP.get(), new Item.Properties())
    );

    // 마늘(식재료 및 파종용)
    public static final Supplier<Item> GARLIC = ITEMS.register(
            "garlic",
            () -> new ItemNameBlockItem(ModCropBlocks.GARLIC_CROP.get(), new Item.Properties().food(ModFoodProperties.GARLIC))
    );

    // 고춧가루
    public static final Supplier<Item> RED_PEPPER_POWDER = ITEMS.register(
            "red_pepper_powder",
            () -> new Item(new Item.Properties())
    );

    public static final Supplier<Item> GOCHUJANG = ITEMS.register("gochujang", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> FISH_SAUCE = ITEMS.register("fish_sauce", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> MINCED_GARLIC = ITEMS.register("minced_garlic", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> TTEOK = ITEMS.register("tteok", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> MINCED_FISH = ITEMS.register("minced_fish", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> RAW_FISH_CAKE = ITEMS.register("raw_fish_cake", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> RAW_KIMCHI_JEON = ITEMS.register("raw_kimchi_jeon", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> RAW_PAJEON = ITEMS.register("raw_pajeon", () -> new Item(new Item.Properties()));

    // 옹기 블록 아이템
    public static final Supplier<Item> ONGGI_BLOCK_ITEM = ITEMS.register(
            "onggi_block",
            () -> new BlockItem(ModBlocks.ONGGI_BLOCK.get(), new Item.Properties())
    );

    // 메주 블록 아이템
    public static final Supplier<Item> MEJU_BLOCK_ITEM = ITEMS.register(
            "meju_block",
            () -> new BlockItem(ModBlocks.MEJU_BLOCK.get(), new Item.Properties())
    );

    // 숙성된 메주
    public static final Supplier<Item> FERMENTED_MEJU = ITEMS.register(
            "fermented_meju_block",
            () -> new BlockItem(ModBlocks.FERMENTED_MEJU_BLOCK.get(), new Item.Properties())
    );

    public static void init() {
        ModFoodItems.init();
    }
}
