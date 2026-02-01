package com.potan.koreandelight.item;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
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

    // 배추
    public static final RegistryObject<Item> KIMCHI_CABBAGE = ITEMS.register(
            "kimchi_cabbage",
            () -> new Item(new Item.Properties().food(ModFoodProperties.KIMCHI_CABBAGE_FOOD))
    );

    // 배추 씨앗
    public static final RegistryObject<Item> KIMCHI_CABBAGE_SEEDS = ITEMS.register(
            "kimchi_cabbage_seeds",
            () -> new ItemNameBlockItem(ModBlocks.KIMCHI_CABBAGE_CROP.get(), new Item.Properties())
    );

    // 고추
    public static final RegistryObject<Item> RED_PEPPER = ITEMS.register(
            "red_pepper",
            () -> new Item(new Item.Properties().food(ModFoodProperties.RED_PEPPER))
    );

    // 고추 씨앗
    public static final RegistryObject<Item> RED_PEPPER_SEEDS = ITEMS.register(
            "red_pepper_seeds",
            () -> new ItemNameBlockItem(ModBlocks.RED_PEPPER_CROP.get(), new Item.Properties())
    );

    // 콩
    public static final RegistryObject<Item> BEAN = ITEMS.register(
            "bean",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BEAN))
    );

    // 콩 씨앗
    public static final RegistryObject<Item> BEAN_SEEDS = ITEMS.register(
            "bean_seeds",
            () -> new ItemNameBlockItem(ModBlocks.BEAN_CROP.get(), new Item.Properties())
    );

    // 대파
    public static final RegistryObject<Item> GREEN_ONION = ITEMS.register(
            "green_onion",
            () -> new Item(new Item.Properties().food(ModFoodProperties.GREEN_ONION))
    );

    // 대파 씨앗
    public static final RegistryObject<Item> GREEN_ONION_SEEDS = ITEMS.register(
            "green_onion_seeds",
            () -> new ItemNameBlockItem(ModBlocks.GREEN_ONION_CROP.get(), new Item.Properties())
    );

    // 고춧가루
    public static final RegistryObject<Item> RED_PEPPER_POWDER = ITEMS.register(
            "red_pepper_powder",
            () -> new Item(new Item.Properties())
    );

    // 김치
    public static final RegistryObject<Item> KIMCHI = ITEMS.register(
            "kimchi",
            () -> new Item(new Item.Properties().food(ModFoodProperties.KIMCHI_FOOD))
    );

    // 겉절이
    public static final RegistryObject<Item> FRESH_KIMCHI = ITEMS.register(
            "fresh_kimchi",
            () -> new Item(new Item.Properties().food(ModFoodProperties.FRESH_KIMCHI))
    );

    // 옹기 블록 아이템
    public static final RegistryObject<Item> ONGGI_BLOCK_ITEM = ITEMS.register(
            "onggi_block",
            () -> new BlockItem(ModBlocks.ONGGI_BLOCK.get(), new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
