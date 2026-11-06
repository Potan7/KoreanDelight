package com.potan.koreandelight.item;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.block.ModCropBlocks;
import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class ModFoodItems {
    public static final RegistrationProvider<Item> ITEMS = Services.PLATFORM.getProvider(Registries.ITEM);

    public static final Supplier<Item> COOKED_RICE_BOWL = ITEMS.register("cooked_rice_bowl",
            () -> new RiceBowlItem(ModBlocks.COOKED_RICE_BOWL.get(), new Item.Properties()
                    .stacksTo(16)
                    .craftRemainder(ModItems.RICE_BOWL.get())
                    .food(ModFoodProperties.cookedRiceBowl())));

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
            () -> new ItemNameBlockItem(ModCropBlocks.BEAN_CROP.get(), new Item.Properties().food(ModFoodProperties.BEAN))
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
    public static final Supplier<Item> KIMCHI_JEON = food("kimchi_jeon", ModFoodProperties.KIMCHI_JEON);
    public static final Supplier<Item> BIBIMBAP = food("bibimbap", ModFoodProperties.BIBIMBAP);
    public static final Supplier<Item> CHICKEN_KALGUKSU = food("chicken_kalguksu", ModFoodProperties.CHICKEN_KALGUKSU);
    public static final Supplier<Item> TTEOKBOKKI = food("tteokbokki", ModFoodProperties.TTEOKBOKKI);
    public static final Supplier<Item> LETTUCE = ITEMS.register("lettuce",
            () -> new SsamLeafItem(new Item.Properties().food(ModFoodProperties.LETTUCE)));
    public static final Supplier<Item> SSAM = ITEMS.register("ssam",
            () -> new SsamItem(new Item.Properties().food(ModFoodProperties.SSAM)));
    public static final Supplier<Item> OPEN_SSAM = ITEMS.register("open_ssam",
            () -> new SsamLeafItem(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> SSAMJANG = food("ssamjang", ModFoodProperties.SSAMJANG);
    public static final Supplier<Item> KIMCHI_BOWL = served("kimchi_bowl", ModBlocks.KIMCHI_BOWL, ModFoodProperties.KIMCHI_FOOD);
    public static final Supplier<Item> GARLIC_BOWL = served("garlic_bowl", ModBlocks.GARLIC_BOWL, ModFoodProperties.GARLIC);
    public static final Supplier<Item> SSAMJANG_BOWL = served("ssamjang_bowl", ModBlocks.SSAMJANG_BOWL, ModFoodProperties.SSAMJANG);
    public static final Supplier<Item> LETTUCE_BOWL = served("lettuce_bowl", ModBlocks.LETTUCE_BOWL,
            ModFoodProperties.LETTUCE, () -> Items.BOWL);
    public static final Supplier<Item> JEYUK_BOKKEUM = ITEMS.register("jeyuk_bokkeum",
            () -> new RiceBowlItem(ModBlocks.JEYUK_BOKKEUM.get(), new Item.Properties().stacksTo(16)
                    .food(ModFoodProperties.JEYUK_BOKKEUM)));
    public static final Supplier<Item> PORK_GUKBAP = food("pork_gukbap", ModFoodProperties.PORK_GUKBAP);
    public static final Supplier<Item> DOENJANG_JJIGAE = food("doenjang_jjigae", ModFoodProperties.DOENJANG_JJIGAE);
    public static final Supplier<Item> SIKHYE = food("sikhye", ModFoodProperties.SIKHYE);

    public static final Supplier<Item> RADISH = food("radish", ModFoodProperties.RADISH);
    public static final Supplier<Item> PERILLA_LEAF = ITEMS.register("perilla_leaf",
            () -> new SsamLeafItem(new Item.Properties().food(ModFoodProperties.PERILLA_LEAF)));
    public static final Supplier<Item> PERILLA_LEAF_BOWL = served("perilla_leaf_bowl", ModBlocks.PERILLA_LEAF_BOWL,
            ModFoodProperties.PERILLA_LEAF, () -> Items.BOWL);
    public static final Supplier<Item> FRESH_KIMCHI = food("fresh_kimchi", ModFoodProperties.FRESH_KIMCHI);
    public static final Supplier<Item> GRILLED_KIMCHI = food("grilled_kimchi", ModFoodProperties.GRILLED_KIMCHI);
    public static final Supplier<Item> GRILLED_GARLIC = food("grilled_garlic", ModFoodProperties.GRILLED_GARLIC);
    public static final Supplier<Item> SUNGNYUNG = ITEMS.register("sungnyung",
            () -> new Item(new Item.Properties().stacksTo(16).craftRemainder(ModItems.RICE_BOWL.get())
                    .food(ModFoodProperties.servedIn(ModFoodProperties.SUNGNYUNG, ModItems.RICE_BOWL.get()))));
    public static final Supplier<Item> DONGCHIMI = ITEMS.register("dongchimi",
            () -> new Item(new Item.Properties().stacksTo(16).craftRemainder(ModItems.RICE_BOWL.get())
                    .food(ModFoodProperties.servedIn(ModFoodProperties.DONGCHIMI, ModItems.RICE_BOWL.get()))));
    public static final Supplier<Item> STEAMED_EGG = ITEMS.register("steamed_egg",
            () -> new Item(new Item.Properties().stacksTo(16).craftRemainder(ModItems.RICE_BOWL.get())
                    .food(ModFoodProperties.servedIn(ModFoodProperties.STEAMED_EGG, ModItems.RICE_BOWL.get()))));

    private static Supplier<Item> food(String name, net.minecraft.world.food.FoodProperties properties) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().food(properties)));
    }

    private static Supplier<Item> served(String name, Supplier<Block> block, FoodProperties food) {
        return served(name, block, food, ModItems.RICE_BOWL);
    }

    private static Supplier<Item> served(String name, Supplier<Block> block, FoodProperties food, Supplier<Item> container) {
        return ITEMS.register(name, () -> new RiceBowlItem(block.get(), new Item.Properties().stacksTo(16)
                .craftRemainder(container.get())
                .food(ModFoodProperties.servedIn(food, container.get()))));
    }


    public static void init() {
        // 클래스 로딩을 위해 호출됩니다.
    }
}
