package com.potan.koreandelight.item;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFoodItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Koreandelight.MODID);

    // 배추
    public static final RegistryObject<Item> KIMCHI_CABBAGE = ITEMS.register(
            "kimchi_cabbage",
            () -> new Item(new Item.Properties().food(ModFoodProperties.KIMCHI_CABBAGE_FOOD))
    );

    // 고추
    public static final RegistryObject<Item> RED_PEPPER = ITEMS.register(
            "red_pepper",
            () -> new Item(new Item.Properties().food(ModFoodProperties.RED_PEPPER))
    );

    // 콩
    public static final RegistryObject<Item> BEAN = ITEMS.register(
            "bean",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BEAN))
    );

    // 대파
    public static final RegistryObject<Item> GREEN_ONION = ITEMS.register(
            "green_onion",
            () -> new Item(new Item.Properties().food(ModFoodProperties.GREEN_ONION))
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

    // 된장
    public static final RegistryObject<Item> DOENJANG = ITEMS.register(
            "doenjang",
            () -> new Item(new Item.Properties().food(ModFoodProperties.DOENJANG))
    );

    // 된장 블록 아이템
    public static final RegistryObject<Item> DOENJANG_BLOCK_ITEM = ITEMS.register(
            "doenjang_block",
            () -> new BlockItem(ModBlocks.DOENJANG_BLOCK.get(), new Item.Properties())
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
