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

    public static final Supplier<Item> RICE_BOWL = ITEMS.register("rice_bowl",
            () -> new RiceBowlItem(ModBlocks.RICE_BOWL.get(), new Item.Properties()));

    public static final Supplier<Item> LETTUCE_SEEDS = ITEMS.register("lettuce_seeds",
            () -> new ItemNameBlockItem(ModCropBlocks.LETTUCE_CROP.get(), new Item.Properties()));
    public static final Supplier<Item> PERILLA_SEEDS = ITEMS.register("perilla_seeds",
            () -> new ItemNameBlockItem(ModCropBlocks.PERILLA_CROP.get(), new Item.Properties()));

    // 간장 양동이
    public static final Supplier<Item> SOY_SAUCE_BUCKET = ITEMS.register(
            "soy_sauce_bucket",
            () -> new BucketItem(ModFluids.SOURCE_SOY_SAUCE.get(), new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1))
    );

    public static final Supplier<Item> SOY_SAUCE_BOTTLE = ITEMS.register("soy_sauce_bottle",
            () -> new FluidBottleItem(ModFluids.SOURCE_SOY_SAUCE));

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
    public static final Supplier<Item> MINCED_GARLIC = ITEMS.register("minced_garlic", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> TTEOK = ITEMS.register("tteok", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> RAW_KIMCHI_JEON = ITEMS.register("raw_kimchi_jeon", () -> new Item(new Item.Properties()));

    // 신규 공정용 재료와 도구. 가공 및 획득 레시피는 각 설비 구현 시 연결합니다.
    public static final Supplier<Item> SALT = ITEMS.register("salt", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> BOILED_BEANS = ITEMS.register("boiled_beans", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> MEJU_LUMP = ITEMS.register("meju_lump", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> MEJU_POWDER = ITEMS.register("meju_powder", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> GLUTINOUS_RICE = ITEMS.register("glutinous_rice", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> STEAMED_RICE = ITEMS.register("steamed_rice", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> STEAMED_GLUTINOUS_RICE = ITEMS.register("steamed_glutinous_rice", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> GLUTINOUS_RICE_PASTE = ITEMS.register("glutinous_rice_paste", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> RICE_FLOUR = ITEMS.register("rice_flour", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> RICE_CAKE_DOUGH = ITEMS.register("rice_cake_dough", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> SESAME = ITEMS.register("sesame", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ROASTED_SESAME = ITEMS.register("roasted_sesame", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> SESAME_OIL = ITEMS.register("sesame_oil",
            () -> new Item(new Item.Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE)));
    public static final Supplier<Item> BRINED_CABBAGE = ITEMS.register("brined_cabbage", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> RAW_SHRIMP = ITEMS.register("raw_shrimp", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ANCHOVY = ITEMS.register("anchovy", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> SALTED_SHRIMP = ITEMS.register("salted_shrimp", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> TOFU = ITEMS.register("tofu", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> MORTAR = ITEMS.register("mortar",
            () -> new BlockItem(ModBlocks.MORTAR.get(), new Item.Properties()));

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
