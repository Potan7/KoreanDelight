package com.potan.koreandelight.block;

import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import com.potan.koreandelight.block.blockentity.RiceBowlBlockEntity;
import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ModBlockEntityTypes {
    public static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            Services.PLATFORM.getProvider(Registries.BLOCK_ENTITY_TYPE);

    public static final Supplier<BlockEntityType<RiceBowlBlockEntity>> RICE_BOWL = BLOCK_ENTITY_TYPES.register(
            "rice_bowl", () -> BlockEntityType.Builder.of(RiceBowlBlockEntity::new,
                    ModBlocks.RICE_BOWL.get(), ModBlocks.COOKED_RICE_BOWL.get(),
                    ModBlocks.KIMCHI_BOWL.get(),
                    ModBlocks.GARLIC_BOWL.get(), ModBlocks.SSAMJANG_BOWL.get(),
                    ModBlocks.LETTUCE_BOWL.get(), ModBlocks.PERILLA_LEAF_BOWL.get(),
                    ModBlocks.JEYUK_BOKKEUM.get()).build(null));

    public static final Supplier<BlockEntityType<com.potan.koreandelight.block.blockentity.MortarBlockEntity>> MORTAR =
            BLOCK_ENTITY_TYPES.register("mortar", () -> BlockEntityType.Builder.of(
                    com.potan.koreandelight.block.blockentity.MortarBlockEntity::new, ModBlocks.MORTAR.get()).build(null));

    // 옹기 블록 엔티티 등록
    public static final Supplier<BlockEntityType<OnggiBlockEntity>> ONGGI_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "onggi_block",
                    () -> BlockEntityType.Builder.of(
                            OnggiBlockEntity::new,
                            ModBlocks.ONGGI_BLOCK.get()
                    ).build(null)
            );

    public static void init() {
        // 클래스 로딩을 위해 호출됩니다.
    }
}
