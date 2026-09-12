package com.potan.koreandelight;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.fluid.ModFluidTypes;
import com.potan.koreandelight.fluid.ModFluids;
import com.potan.koreandelight.item.ModFoodItems;
import com.potan.koreandelight.item.ModItems;
import com.potan.koreandelight.recipe.ModRecipes;
import com.potan.koreandelight.mobeffect.ModEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Korean Delight 모드의 공통(Common) 메인 클래스입니다.
 * Forge/NeoForge/Fabric 모든 플랫폼에서 공통으로 실행되는 로직을 담당합니다.
 */
public class Koreandelight {
    public static final String MODID = "koreandelight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    /**
     * 모드 초기화 메서드.
     * 각 플랫폼 모듈(Fabric, NeoForge)의 메인 클래스에서 호출됩니다.
     */
    public static void init() {
        LOGGER.info("Korean Delight: Initializing common logic for 1.21.1 Multiloader!");

        // 1. 유체(Fluid) 초기화
        ModFluidTypes.init();
        ModFluids.init();

        // 2. 블록(Block) 초기화
        ModBlocks.init();

        // 3. 아이템(Item) 초기화
        ModItems.init();
        ModFoodItems.init();

        // 4. 나머지 초기화
        ModEffects.init();
        ModBlockEntityTypes.init();
        ModRecipes.init();
        com.potan.koreandelight.menu.ModMenuTypes.init();

        // 크리에이티브 탭 등록
        ModCreativeTabs.init();
    }
}
