package com.potan.koreandelight.block;

import com.potan.koreandelight.block.custom.cropblock.BeanCropBlock;
import com.potan.koreandelight.block.custom.cropblock.GreenOnionCropBlock;
import com.potan.koreandelight.block.custom.cropblock.KimchiCabbageCropBlock;
import com.potan.koreandelight.block.custom.cropblock.RedPepperCropBlock;
import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class ModCropBlocks {
    public static final RegistrationProvider<Block> BLOCKS = Services.PLATFORM.getProvider(Registries.BLOCK);

    // 배추 작물
    public static final Supplier<Block> KIMCHI_CABBAGE_CROP = BLOCKS.register(
            "kimchi_cabbage_crop",
            () -> new KimchiCabbageCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noOcclusion().noCollission())
    );

    // 고추 작물
    public static final Supplier<Block> RED_PEPPER_CROP = BLOCKS.register(
            "red_pepper_crop",
            () -> new RedPepperCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noOcclusion().noCollission())
    );

    // 콩 작물
    public static final Supplier<Block> BEAN_CROP = BLOCKS.register(
            "bean_crop",
            () -> new BeanCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noOcclusion().noCollission())
    );

    // 대파 작물
    public static final Supplier<Block> GREEN_ONION_CROP = BLOCKS.register(
            "green_onion_crop",
            () -> new GreenOnionCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).noOcclusion().noCollission())
    );

    public static void init() {
        // 클래스 로딩을 위해 호출됩니다.
    }
}
