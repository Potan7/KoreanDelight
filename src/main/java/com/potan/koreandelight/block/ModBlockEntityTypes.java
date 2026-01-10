package com.potan.koreandelight.block;

import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "koreandelight");

    public static final RegistryObject<BlockEntityType<OnggiBlockEntity>> ONGGI_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("onggi_block",
                    () -> BlockEntityType.Builder.of(
                            OnggiBlockEntity::new,
                            ModBlocks.ONGGI_BLOCK.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
