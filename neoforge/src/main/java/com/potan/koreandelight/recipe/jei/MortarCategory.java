package com.potan.koreandelight.recipe.jei;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class MortarCategory implements IRecipeCategory<MortarCategory.Recipe> {
    public static final RecipeType<Recipe> TYPE = RecipeType.create(Koreandelight.MODID, "mortar", Recipe.class);
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawable arrow;

    public MortarCategory(IGuiHelper helper) {
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.MORTAR.get()));
        slot = helper.getSlotDrawable();
        arrow = helper.getRecipeArrow();
    }

    @Override public RecipeType<Recipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei.koreandelight.category.mortar"); }
    @Override public int getWidth() { return 160; }
    @Override public int getHeight() { return 44; }
    @Override public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 13, 19).addItemStack(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 131, 19).addItemStack(recipe.output());
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        slot.draw(graphics, 12, 18);
        slot.draw(graphics, 130, 18);
        arrow.draw(graphics, 38, 19);
        icon.draw(graphics, 72, 19);
        arrow.draw(graphics, 98, 19);

    }

    public record Recipe(ItemStack input, ItemStack output) {}
}
