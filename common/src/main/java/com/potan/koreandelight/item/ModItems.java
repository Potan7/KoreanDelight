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

    // 콩 씨앗
    public static final Supplier<Item> BEAN_SEEDS = ITEMS.register(
            "bean_seeds",
            () -> new ItemNameBlockItem(ModCropBlocks.BEAN_CROP.get(), new Item.Properties())
    );

    // 대파 씨앗
    public static final Supplier<Item> GREEN_ONION_SEEDS = ITEMS.register(
            "green_onion_seeds",
            () -> new ItemNameBlockItem(ModCropBlocks.GREEN_ONION_CROP.get(), new Item.Properties())
    );

    // 고춧가루
    public static final Supplier<Item> RED_PEPPER_POWDER = ITEMS.register(
            "red_pepper_powder",
            () -> new Item(new Item.Properties())
    );

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

    // 김장 대야 블록 아이템
    public static final Supplier<Item> KIMJANG_BASIN = ITEMS.register(
            "kimjang_basin",
            () -> new BlockItem(ModBlocks.KIMJANG_BASIN.get(), new Item.Properties())
    );

    public static void init() {
        ModFoodItems.init();
    }
}
