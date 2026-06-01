package com.potan.koreandelight.block;

import com.potan.koreandelight.block.custom.KimjangBasin;
import com.potan.koreandelight.block.custom.MejuBlock;
import com.potan.koreandelight.block.custom.OnggiBlock;
import com.potan.koreandelight.fluid.ModFluids;
import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class ModBlocks {
    public static final RegistrationProvider<Block> BLOCKS = Services.PLATFORM.getProvider(Registries.BLOCK);

    // 간장 액체 블록
    public static final Supplier<LiquidBlock> SOY_SAUCE_BLOCK = BLOCKS.register(
            "soy_sauce_block",
            () -> new com.potan.koreandelight.block.custom.ModLiquidBlock(
                    ModFluids.SOURCE_SOY_SAUCE.get(),
                    BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.WATER).noLootTable()
            )
    );

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
            () -> new Block(BlockBehaviour.Properties.of().noOcclusion().strength(0.2f))
    );

    // 된장 블록
    public static final Supplier<Block> DOENJANG_BLOCK = BLOCKS.register(
            "doenjang_block",
            () -> new Block(BlockBehaviour.Properties.of().noOcclusion().strength(0.5f))
    );

    // 김장 대야 블록
    public static final Supplier<Block> KIMJANG_BASIN = BLOCKS.register(
            "kimjang_basin",
            () -> new KimjangBasin(BlockBehaviour.Properties.of().noOcclusion().strength(0.5f))
    );

    public static void init() {
        ModCropBlocks.init();
    }
}
