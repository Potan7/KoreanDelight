package com.potan.koreandelight.block;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.custom.*;
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

    // Creates a new Block with the id "koreandelight:example_block", combining the namespace and path
//    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    // 간장 액체 블록
    public static final RegistryObject<LiquidBlock> SOY_SAUCE_BLOCK = BLOCKS.register("soy_sauce_block",
            () -> new LiquidBlock(ModFluids.SOURCE_SOY_SAUCE, BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));

    // 배추 작물
    public static final RegistryObject<Block> KIMCHI_CABBAGE_CROP = BLOCKS.register("kimchi_cabbage_crop",
            () -> new KimchiCabbageCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));

    // 고추 작물
    public static final RegistryObject<Block> RED_PEPPER_CROP = BLOCKS.register("red_pepper_crop",
            () -> new RedPepperCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));

    // 콩 작물
    public static final RegistryObject<Block> BEAN_CROP = BLOCKS.register("bean_crop",
            () -> new BeanCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));

    // 대파 작물
    public static final RegistryObject<Block> GREEN_ONION_CROP = BLOCKS.register("green_onion_crop",
            () -> new GreenOnionCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));

    // 옹기 블록
    public static final RegistryObject<Block> ONGGI_BLOCK = BLOCKS.register("onggi_block",
            () -> new OnggiBlock(BlockBehaviour.Properties.of().noOcclusion().strength(0.5f)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }


}
