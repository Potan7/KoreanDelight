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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class KimjangCategory implements IRecipeCategory<KimjangCategory.Recipe> {
    public static final RecipeType<Recipe> TYPE = RecipeType.create(Koreandelight.MODID, "kimjang", Recipe.class);
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawable arrow;

    public KimjangCategory(IGuiHelper helper) {
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.KIMJANG_BASIN.get()));
        slot = helper.getSlotDrawable();
        arrow = helper.getRecipeArrow();
    }

    @Override public RecipeType<Recipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return ModBlocks.KIMJANG_BASIN.get().getName(); }
    @Override public int getWidth() { return 156; }
    @Override public int getHeight() { return 43; }
    @Override public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < recipe.inputs().size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, 9 + i * 18, 22).addItemStack(recipe.inputs().get(i));
        }
        for (int i = 0; i < recipe.outputs().size(); i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 109 + i * 18, 22).addItemStack(recipe.outputs().get(i));
        }
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.drawCenteredString(Minecraft.getInstance().font, recipe.instruction(), getWidth() / 2, 5, 0xFFFFFF);
        for (int i = 0; i < recipe.inputs().size(); i++) {
            slot.draw(graphics, 8 + i * 18, 21);
        }
        for (int i = 0; i < recipe.outputs().size(); i++) {
            slot.draw(graphics, 108 + i * 18, 21);
        }
        arrow.draw(graphics, 73, 22);
    }

    public record Recipe(Component instruction, List<ItemStack> inputs, List<ItemStack> outputs) {}
}
