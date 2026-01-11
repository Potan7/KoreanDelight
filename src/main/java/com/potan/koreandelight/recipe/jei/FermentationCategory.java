package com.potan.koreandelight.recipe.jei;

import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.recipe.FermentationRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FermentationCategory implements IRecipeCategory<FermentationRecipe> {
    public static final RecipeType<FermentationRecipe> TYPE = RecipeType.create("koreandelight", "fermentation", FermentationRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component localizedName;

    public FermentationCategory(IGuiHelper helper) {
        // 1. 배경 설정: 텍스처 경로, u, v, width, height
        // (본인의 GUI 텍스처를 사용하세요. 여기선 예시입니다.)
        ResourceLocation texture = new ResourceLocation("koreandelight", "textures/gui/fermentation_jei.png");
        this.background = helper.createDrawable(texture, 0, 0, 176, 80); // 좌표와 크기는 텍스처에 맞게 조절

        // 2. 아이콘 설정: 카테고리 탭에 뜰 아이콘 (옹기 블록)
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ONGGI_BLOCK.get()));

        // 3. 이름 설정
        this.localizedName = Component.translatable("jei.koreandelight.category.fermentation");
    }

    @Override
    public RecipeType<FermentationRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FermentationRecipe recipe, IFocusGroup focuses) {
        // 1. 재료(Input) 슬롯 위치 잡기 (x, y는 배경 이미지 기준 상대 좌표)
        builder.addSlot(RecipeIngredientRole.INPUT, 40, 20)
                .addIngredients(recipe.getIngredients().get(0));

        // 2. 결과(Output) 슬롯 위치 잡기
        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 20)
                .addItemStack(recipe.getResultItem(null)); // RegistryAccess는 null이어도 보통 괜찮음
    }
}
