package com.potan.koreandelight.block;

import com.potan.koreandelight.block.custom.MejuBlock;
import com.potan.koreandelight.block.custom.MejuBaseBlock;
import com.potan.koreandelight.block.custom.OnggiBlock;
import com.potan.koreandelight.block.custom.RiceBowlBlock;
import com.potan.koreandelight.fluid.ModFluids;
import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class ModBlocks {
    public static final RegistrationProvider<Block> BLOCKS = Services.PLATFORM.getProvider(Registries.BLOCK);

    public static final Supplier<Block> RICE_BOWL = BLOCKS.register("rice_bowl",
            () -> new RiceBowlBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.2F).sound(SoundType.DECORATED_POT)));
    public static final Supplier<Block> COOKED_RICE_BOWL = BLOCKS.register("cooked_rice_bowl",
            () -> new RiceBowlBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.2F).sound(SoundType.DECORATED_POT),
                    RiceBowlBlock.DEFAULT_SHAPE, RiceBowlBlock.DISH_SERVINGS));

    public static final Supplier<Block> KIMCHI_BOWL = bowl("kimchi_bowl");
    public static final Supplier<Block> GARLIC_BOWL = bowl("garlic_bowl");
    public static final Supplier<Block> SSAMJANG_BOWL = bowl("ssamjang_bowl");
    public static final Supplier<Block> LETTUCE_BOWL = BLOCKS.register("lettuce_bowl",
            () -> new RiceBowlBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.2F)
                    .sound(SoundType.WOOD), Block.box(2, 0, 2, 14, 4, 14), RiceBowlBlock.DISH_SERVINGS));
    public static final Supplier<Block> PERILLA_LEAF_BOWL = BLOCKS.register("perilla_leaf_bowl",
            () -> new RiceBowlBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.2F)
                    .sound(SoundType.WOOD), Block.box(2, 0, 2, 14, 4, 14), RiceBowlBlock.DISH_SERVINGS));
    public static final Supplier<Block> JEYUK_BOKKEUM = BLOCKS.register("jeyuk_bokkeum",
            () -> new RiceBowlBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.2F)
                    .sound(SoundType.WOOD), Block.box(1, 0, 1.5, 15, 7, 14.5), RiceBowlBlock.DISH_SERVINGS));

    private static Supplier<Block> bowl(String name) {
        return BLOCKS.register(name, () -> new RiceBowlBlock(BlockBehaviour.Properties.of()
                .noOcclusion().strength(0.2F).sound(SoundType.DECORATED_POT),
                RiceBowlBlock.DEFAULT_SHAPE, RiceBowlBlock.DISH_SERVINGS));
    }

    // 간장 액체 블록
    public static final Supplier<LiquidBlock> SOY_SAUCE_BLOCK = BLOCKS.register(
            "soy_sauce_block",
            () -> new com.potan.koreandelight.block.custom.ModLiquidBlock(
                    ModFluids.SOURCE_SOY_SAUCE.get(),
                    BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WATER).noLootTable()
            )
    );

    public static final Supplier<Block> MORTAR = BLOCKS.register("mortar",
            () -> new com.potan.koreandelight.block.custom.MortarBlock(BlockBehaviour.Properties.of().noOcclusion().strength(2.0F).sound(SoundType.STONE)));

    // 옹기 블록
    public static final Supplier<Block> ONGGI_BLOCK = BLOCKS.register(
            "onggi_block",
            () -> new OnggiBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.5f))
    );

    // 메주 블록
    public static final Supplier<Block> MEJU_BLOCK = BLOCKS.register(
            "meju_block",
            () -> new MejuBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.2f))
    );

    // 발효된 메주 블록
    public static final Supplier<Block> FERMENTED_MEJU_BLOCK = BLOCKS.register(
            "fermented_meju_block",
            () -> new MejuBaseBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.2f))
    );

    // 된장 블록
    public static final Supplier<Block> DOENJANG_BLOCK = BLOCKS.register(
            "doenjang_block",
            () -> new Block(BlockBehaviour.Properties.of().noOcclusion().strength(0.5f))
    );

    public static void init() {
        ModCropBlocks.init();
    }
}
