package com.potan.koreandelight.recipe.jei;

import com.potan.koreandelight.Koreandelight;
import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.recipe.FermentationRecipe;
import com.potan.koreandelight.recipe.FluidStackData;
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
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class FermentationCategory implements IRecipeCategory<FermentationRecipe> {
    public static final RecipeType<FermentationRecipe> TYPE =
            RecipeType.create(Koreandelight.MODID, "fermentation", FermentationRecipe.class);

    private static final int WIDTH = 176;
    private static final int HEIGHT = 140;
    private static final int SPACING = 36;
    private final IDrawable icon;

    public FermentationCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ONGGI_BLOCK.get()));
    }

    @Override
    public RecipeType<FermentationRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.koreandelight.category.fermentation");
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
    public void setRecipe(IRecipeLayoutBuilder builder, FermentationRecipe recipe, IFocusGroup focuses) {
        List<IngredientAmount> inputs = groupIngredients(recipe);
        int inputCount = inputs.size() + (recipe.getFluid().isPresent() ? 1 : 0);
        for (int i = 0; i < inputs.size(); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, slotX(inputCount, i), 14)
                    .addIngredients(inputs.get(i).ingredient());
        }
        recipe.getFluid().ifPresent(fluid -> addFluidSlot(builder, RecipeIngredientRole.INPUT,
                slotX(inputCount, inputs.size()), 14, fluid));

        int itemOutputs = recipe.getOutput().isEmpty() ? 0 : 1;
        int outputCount = itemOutputs + (recipe.getResultFluid().isPresent() ? 1 : 0);
        if (itemOutputs > 0) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, slotX(outputCount, 0), 80)
                    .addItemStack(recipe.getOutput().copyWithCount(1));
        }
        recipe.getResultFluid().ifPresent(fluid -> addFluidSlot(builder, RecipeIngredientRole.OUTPUT,
                slotX(outputCount, itemOutputs), 80, fluid));
    }

    @Override
    public void draw(FermentationRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        List<IngredientAmount> inputs = groupIngredients(recipe);
        List<String> inputAmounts = new ArrayList<>();
        inputs.forEach(input -> inputAmounts.add(Integer.toString(input.count())));
        recipe.getFluid().ifPresent(fluid -> inputAmounts.add(fluidAmount(fluid)));
        drawRatio(graphics, inputAmounts, 14, 0xFFDED2BE);

        List<String> outputAmounts = new ArrayList<>();
        if (!recipe.getOutput().isEmpty()) {
            outputAmounts.add(Integer.toString(recipe.getOutput().getCount()));
        }
        recipe.getResultFluid().ifPresent(fluid -> outputAmounts.add(fluidAmount(fluid)));
        drawRatio(graphics, outputAmounts, 80, 0xFFD7DFC5);

        // A downward arrow connects the complete input ratio to the complete output ratio.
        int center = WIDTH / 2;
        graphics.fill(center - 1, 53, center + 2, 67, 0xFF77904D);
        for (int i = 0; i < 5; i++) {
            graphics.fill(center - 5 + i, 65 + i, center + 6 - i, 66 + i, 0xFF77904D);
        }
        graphics.drawWordWrap(Minecraft.getInstance().font,
                Component.translatable("jei.koreandelight.fermentation.environment",
                        Component.translatable(recipe.getEnvironment().getTranslationKey())),
                8, 118, WIDTH - 16, 0x493C2C);
    }

    private static void drawRatio(GuiGraphics graphics, List<String> amounts, int y, int color) {
        var font = Minecraft.getInstance().font;
        for (int i = 0; i < amounts.size(); i++) {
            int x = slotX(amounts.size(), i);
            graphics.fill(x - 2, y - 2, x + 18, y + 18, 0xFF645747);
            graphics.fill(x - 1, y - 1, x + 17, y + 17, color);
            graphics.drawCenteredString(font, amounts.get(i), x + 8, y + 22, 0x493C2C);
            if (i + 1 < amounts.size()) {
                graphics.drawCenteredString(font, ":", x + 8 + SPACING / 2, y + 5, 0x493C2C);
            }
        }
    }

    private static int slotX(int count, int index) {
        return WIDTH / 2 - 8 - (count - 1) * SPACING / 2 + index * SPACING;
    }

    private static String fluidAmount(FluidStackData fluid) {
        return fluid.amount() % 1000 == 0 ? fluid.amount() / 1000 + " B" : fluid.amount() + " mB";
    }

    private record IngredientAmount(Ingredient ingredient, int count) {}

    private static List<IngredientAmount> groupIngredients(FermentationRecipe recipe) {
        List<IngredientAmount> grouped = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient.isEmpty()) {
                continue;
            }
            int index = 0;
            while (index < grouped.size() && !sameIngredient(grouped.get(index).ingredient(), ingredient)) {
                index++;
            }
            if (index == grouped.size()) {
                grouped.add(new IngredientAmount(ingredient, 1));
            } else {
                grouped.set(index, new IngredientAmount(grouped.get(index).ingredient(), grouped.get(index).count() + 1));
            }
        }
        return grouped;
    }

    private static boolean sameIngredient(Ingredient first, Ingredient second) {
        ItemStack[] a = first.getItems();
        ItemStack[] b = second.getItems();
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!ItemStack.isSameItemSameComponents(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    private static void addFluidSlot(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y, FluidStackData fluid) {
        builder.addSlot(role, x, y)
                .setFluidRenderer(fluid.amount(), false, 16, 16)
                .addFluidStack(fluid.fluid(), fluid.amount());
    }
}
