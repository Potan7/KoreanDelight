package com.potan.koreandelight.block;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.custom.MejuBlock;
import com.potan.koreandelight.block.custom.OnggiBlock;
import com.potan.koreandelight.fluid.ModFluids;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    // Create a Deferred Register to hold Blocks which will all be registered under the "koreandelight" namespace
    // 모든 블록이 "koreandelight" 네임스페이스 아래에 등록되도록 Deferred Register를 생성합니다.
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Koreandelight.MODID);

    // 간장 액체 블록
    public static final RegistryObject<LiquidBlock> SOY_SAUCE_BLOCK = BLOCKS.register("soy_sauce_block",
            () -> new LiquidBlock(ModFluids.SOURCE_SOY_SAUCE, BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));

    // 옹기 블록
    public static final RegistryObject<Block> ONGGI_BLOCK = BLOCKS.register("onggi_block",
            () -> new OnggiBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.5f)));

    // 메주 블록
    public static final RegistryObject<Block> MEJU_BLOCK = BLOCKS.register("meju_block",
            () -> new MejuBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.2f)));

    // 발효된 메주 블록
    public static final RegistryObject<Block> FERMENTED_MEJU_BLOCK = BLOCKS.register("fermented_meju_block",
            () -> new Block(BlockBehaviour.Properties.of().noOcclusion().strength(0.2f)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ModCropBlocks.register(eventBus);
    }
}