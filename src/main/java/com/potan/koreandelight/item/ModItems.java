package com.potan.koreandelight.item;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.block.ModCropBlocks;
import com.potan.koreandelight.fluid.ModFluids;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    // Create a Deferred Register to hold Items which will all be registered under the "koreandelight" namespace
    // 모든 아이템이 "koreandelight" 네임스페이스 아래에 등록되도록 Deferred Register를 생성합니다.
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Koreandelight.MODID);

    // 간장 양동이
    public static final RegistryObject<Item> SOY_SAUCE_BUCKET = ITEMS.register("soy_sauce_bucket",
            () -> new BucketItem(ModFluids.SOURCE_SOY_SAUCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    // 배추 씨앗
    public static final RegistryObject<Item> KIMCHI_CABBAGE_SEEDS = ITEMS.register(
            "kimchi_cabbage_seeds",
            () -> new ItemNameBlockItem(ModCropBlocks.KIMCHI_CABBAGE_CROP.get(), new Item.Properties())
    );

    // 고추 씨앗
    public static final RegistryObject<Item> RED_PEPPER_SEEDS = ITEMS.register(
            "red_pepper_seeds",
            () -> new ItemNameBlockItem(ModCropBlocks.RED_PEPPER_CROP.get(), new Item.Properties())
    );

    // 콩 씨앗
    public static final RegistryObject<Item> BEAN_SEEDS = ITEMS.register(
            "bean_seeds",
            () -> new ItemNameBlockItem(ModCropBlocks.BEAN_CROP.get(), new Item.Properties())
    );

    // 대파 씨앗
    public static final RegistryObject<Item> GREEN_ONION_SEEDS = ITEMS.register(
            "green_onion_seeds",
            () -> new ItemNameBlockItem(ModCropBlocks.GREEN_ONION_CROP.get(), new Item.Properties())
    );

    // 고춧가루
    public static final RegistryObject<Item> RED_PEPPER_POWDER = ITEMS.register(
            "red_pepper_powder",
            () -> new Item(new Item.Properties())
    );

    // 옹기 블록 아이템
    public static final RegistryObject<Item> ONGGI_BLOCK_ITEM = ITEMS.register(
            "onggi_block",
            () -> new BlockItem(ModBlocks.ONGGI_BLOCK.get(), new Item.Properties())
    );

    // 메주 블록 아이템
    public static final RegistryObject<Item> MEJU_BLOCK_ITEM = ITEMS.register(
            "meju_block",
            () -> new BlockItem(ModBlocks.MEJU_BLOCK.get(), new Item.Properties())
    );

    // 숙성된 메주
    public static final RegistryObject<Item> FERMENTED_MEJU = ITEMS.register(
            "fermented_meju_block",
            () -> new BlockItem(ModBlocks.FERMENTED_MEJU_BLOCK.get(), new Item.Properties())
    );

    // 김장 대야 블록 아이템
    public static final RegistryObject<Item> KIMJANG_BASIN = ITEMS.register(
            "kimjang_basin",
            () -> new BlockItem(ModBlocks.KIMJANG_BASIN.get(), new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        ModFoodItems.register(eventBus);
    }

}