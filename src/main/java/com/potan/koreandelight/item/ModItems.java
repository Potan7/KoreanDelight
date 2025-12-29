package com.potan.koreandelight.item;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    // Create a Deferred Register to hold Items which will all be registered under the "koreandelight" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Koreandelight.MODID);

    // Creates a new BlockItem with the id "koreandelight:example_block", combining the namespace and path
//    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> TEST_ITEM = ITEMS.register(
            "test_item",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> KIMCHI_CABBAGE = ITEMS.register(
            "kimchi_cabbage",
            () -> new Item(new Item.Properties().food(ModFoodProperties.KIMCHI_CABBAGE_FOOD))
    );

    public static final RegistryObject<Item> KIMCHI_CABBAGE_SEEDS = ITEMS.register(
            "kimchi_cabbage_seeds",
            () -> new ItemNameBlockItem(ModBlocks.KIMCHI_CABBAGE_CROP.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> RED_PEPPER = ITEMS.register(
            "red_pepper",
            () -> new Item(new Item.Properties().food(ModFoodProperties.RED_PEPPER))
    );

    public static final RegistryObject<Item> RED_PEPPER_SEEDS = ITEMS.register(
            "red_pepper_seeds",
            () -> new ItemNameBlockItem(ModBlocks.RED_PEPPER_CROP.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> RED_PEPPER_POWDER = ITEMS.register(
            "red_pepper_powder",
            () -> new Item(new Item.Properties())
    );

    public static final RegistryObject<Item> KIMCHI = ITEMS.register(
            "kimchi",
            () -> new Item(new Item.Properties().food(ModFoodProperties.KIMCHI_FOOD))
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
