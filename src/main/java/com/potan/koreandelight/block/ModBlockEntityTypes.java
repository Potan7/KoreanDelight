package com.potan.koreandelight.block;

import com.potan.koreandelight.block.blockentity.KimjangBasinEntity;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {
    // 블록 엔티티 타입 레지스트리 생성
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "koreandelight");

    // 옹기 블록 엔티티 등록
    public static final RegistryObject<BlockEntityType<OnggiBlockEntity>> ONGGI_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("onggi_block",
                    () -> BlockEntityType.Builder.of(
                            OnggiBlockEntity::new,
                            ModBlocks.ONGGI_BLOCK.get()
                    ).build(null)
            );

    // 김장 대야 엔티티 등록
    public static final RegistryObject<BlockEntityType<KimjangBasinEntity>> KIMJANG_BASIN_ENTITY =
            BLOCK_ENTITY_TYPES.register("kimjang_basin",
                    () -> BlockEntityType.Builder.of(
                            KimjangBasinEntity::new,
                            ModBlocks.KIMJANG_BASIN.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
