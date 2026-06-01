package com.potan.koreandelight.recipe.jei;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.recipe.FermentationRecipe;
import com.potan.koreandelight.recipe.FluidStackData;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FermentationCategory implements IRecipeCategory<FermentationRecipe> {
    public static final RecipeType<FermentationRecipe> TYPE =
            RecipeType.create(Koreandelight.MODID, "fermentation", FermentationRecipe.class);

    private static final int WIDTH = 176;
    private static final int HEIGHT = 80;

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public FermentationCategory(IGuiHelper helper) {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(
                Koreandelight.MODID,
                "textures/gui/fermentation_jei.png"
        );
        this.background = helper.createDrawable(texture, 0, 0, WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ONGGI_BLOCK.get()));
        this.title = Component.translatable("jei.koreandelight.category.fermentation");
    }

    @Override
    public RecipeType<FermentationRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, FermentationRecipe recipe, IFocusGroup focuses) {
        builder.addDrawable(background, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FermentationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 46, 12)
                .addIngredients(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 103, 12)
                .addItemStack(recipe.getOutput());

        recipe.getFluid().ifPresent(fluid -> addFluidSlot(builder, RecipeIngredientRole.INPUT, 46, 50, fluid));
        recipe.getResultFluid().ifPresent(fluid -> addFluidSlot(builder, RecipeIngredientRole.OUTPUT, 103, 50, fluid));
    }

    private static void addFluidSlot(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y, FluidStackData fluid) {
        builder.addSlot(role, x, y)
                .setFluidRenderer(fluid.amount(), false, 16, 16)
                .addFluidStack(fluid.fluid(), fluid.amount());
    }
}
