package com.potan.koreandelight.block;

import com.potan.koreandelight.block.blockentity.KimjangBasinEntity;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import com.potan.koreandelight.platform.RegistrationProvider;
import com.potan.koreandelight.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ModBlockEntityTypes {
    public static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            Services.PLATFORM.getProvider(Registries.BLOCK_ENTITY_TYPE);

    // 옹기 블록 엔티티 등록
    public static final Supplier<BlockEntityType<OnggiBlockEntity>> ONGGI_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "onggi_block",
                    () -> BlockEntityType.Builder.of(
                            OnggiBlockEntity::new,
                            ModBlocks.ONGGI_BLOCK.get()
                    ).build(null)
            );

    // 김장 대야 엔티티 등록
    public static final Supplier<BlockEntityType<KimjangBasinEntity>> KIMJANG_BASIN_ENTITY =
            BLOCK_ENTITY_TYPES.register(
                    "kimjang_basin",
                    () -> BlockEntityType.Builder.of(
                            KimjangBasinEntity::new,
                            ModBlocks.KIMJANG_BASIN.get()
                    ).build(null)
            );

    public static void init() {
        // 클래스 로딩을 위해 호출됩니다.
    }
}
