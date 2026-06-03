package com.potan.koreandelight.item;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ModFoodItems {
    public static final RegistrationProvider<Item> ITEMS = Services.PLATFORM.getProvider(Registries.ITEM);

    // 배추
    public static final Supplier<Item> KIMCHI_CABBAGE = ITEMS.register(
            "kimchi_cabbage",
            () -> new Item(new Item.Properties().food(ModFoodProperties.KIMCHI_CABBAGE_FOOD))
    );

    // 고추
    public static final Supplier<Item> RED_PEPPER = ITEMS.register(
            "red_pepper",
            () -> new Item(new Item.Properties().food(ModFoodProperties.RED_PEPPER))
    );

    // 콩
    public static final Supplier<Item> BEAN = ITEMS.register(
            "bean",
            () -> new Item(new Item.Properties().food(ModFoodProperties.BEAN))
    );

    // 대파
    public static final Supplier<Item> GREEN_ONION = ITEMS.register(
            "green_onion",
            () -> new Item(new Item.Properties().food(ModFoodProperties.GREEN_ONION))
    );

    // 김치
    public static final Supplier<Item> KIMCHI = ITEMS.register(
            "kimchi",
            () -> new Item(new Item.Properties().food(ModFoodProperties.KIMCHI_FOOD))
    );

    // 묵은지
    public static final Supplier<Item> AGED_KIMCHI = ITEMS.register(
            "aged_kimchi",
            () -> new Item(new Item.Properties().food(ModFoodProperties.AGED_KIMCHI_FOOD))
    );

    // 된장
    public static final Supplier<Item> DOENJANG = ITEMS.register(
            "doenjang",
            () -> new Item(new Item.Properties().food(ModFoodProperties.DOENJANG))
    );

    // 된장 블록 아이템
    public static final Supplier<Item> DOENJANG_BLOCK_ITEM = ITEMS.register(
            "doenjang_block",
            () -> new BlockItem(ModBlocks.DOENJANG_BLOCK.get(), new Item.Properties())
    );

    public static void init() {
        // 클래스 로딩을 위해 호출됩니다.
    }
}
