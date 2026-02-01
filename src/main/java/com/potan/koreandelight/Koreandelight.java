package com.potan.koreandelight;

import com.mojang.logging.LogUtils;
import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.fluid.ModFluidTypes;
import com.potan.koreandelight.fluid.ModFluids;
import com.potan.koreandelight.item.ModItems;
import com.potan.koreandelight.recipe.ModRecipes;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
// META-INF/mods.toml 파일에 있는 entry와 일치해야 합니다.
@Mod(Koreandelight.MODID)
public class Koreandelight {

    // Define mod id in a common place for everything to reference
    // 모든 곳에서 참조할 수 있도록 Mod ID를 정의합니다.
    public static final String MODID = "koreandelight";
    // Directly reference a slf4j logger
    // slf4j 로거를 직접 참조합니다.
    private static final Logger LOGGER = LogUtils.getLogger();

    public Koreandelight(FMLJavaModLoadingContext ctx) {
        IEventBus modEventBus = ctx.getModEventBus();

        // Register the commonSetup method for modloading
        // 모드 로딩을 위한 commonSetup 메서드를 등록합니다.
        modEventBus.addListener(this::commonSetup);

        // 레지스트리 등록 (유체, 블록, 아이템, 탭, 레시피 등)
        ModFluidTypes.register(modEventBus);
        ModFluids.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntityTypes.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModRecipes.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        // 서버 및 기타 게임 이벤트에 등록합니다.
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        // 아이템을 크리에이티브 탭에 등록합니다.
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        // Forge가 구성 파일을 생성하고 로드할 수 있도록 Mod의 ForgeConfigSpec을 등록합니다.
        ctx.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        // 공통 설정 코드
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    // 예제 블록 아이템을 건축 블록 탭에 추가합니다.
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    // SubscribeEvent를 사용하여 이벤트 버스가 호출할 메서드를 찾도록 할 수 있습니다.
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        // 서버가 시작될 때 수행할 작업
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    // EventBusSubscriber를 사용하여 @SubscribeEvent 주석이 달린 클래스의 모든 정적 메서드를 자동으로 등록할 수 있습니다.
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            // 클라이언트 설정 코드
//            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

//            ItemBlockRenderTypes.setRenderLayer(ModBlocks.KIMCHI_CABBAGE_CROP.get(), RenderType.cutout());
        }
    }
}
