package com.potan.koreandelight.block;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.custom.cropblock.BeanCropBlock;
import com.potan.koreandelight.block.custom.cropblock.GreenOnionCropBlock;
import com.potan.koreandelight.block.custom.cropblock.KimchiCabbageCropBlock;
import com.potan.koreandelight.block.custom.cropblock.RedPepperCropBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModCropBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Koreandelight.MODID);

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

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
