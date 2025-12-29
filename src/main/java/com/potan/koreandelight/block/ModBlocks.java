package com.potan.koreandelight.block;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.custom.KimchiCabbageCropBlock;
import com.potan.koreandelight.block.custom.RedPepperCropBlock;
import io.netty.util.Attribute;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    // Create a Deferred Register to hold Blocks which will all be registered under the "koreandelight" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Koreandelight.MODID);

    // Creates a new Block with the id "koreandelight:example_block", combining the namespace and path
//    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    public static final RegistryObject<Block> KIMCHI_CABBAGE_CROP = BLOCKS.register("kimchi_cabbage_crop",
            () -> new KimchiCabbageCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));

    public static final RegistryObject<Block> RED_PEPPER_CROP = BLOCKS.register("red_pepper_crop",
            () -> new RedPepperCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }


}
