package com.potan.koreandelight.item;

import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.block.custom.RiceBowlBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SsamLeafItem extends Item {
    public static final int MEAT = 1;
    public static final int RICE = 2;
    public static final int KIMCHI = 8;
    public static final int GARLIC = 16;
    public static final int SSAMJANG = 32;
    public static final int COMPLETE = MEAT | RICE | GARLIC | SSAMJANG;
    private static final int ALL_INGREDIENTS = COMPLETE | KIMCHI;
    private static final int LEGACY_CONDIMENTS = 4;
    private static final String INGREDIENTS = "SsamIngredients";
    private static final int[] INGREDIENT_ORDER = {MEAT, RICE, GARLIC, SSAMJANG, KIMCHI};

    public SsamLeafItem(Properties properties) {
        super(properties);
    }

    public static int getIngredients(ItemStack stack) {
        int contents = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(INGREDIENTS);
        // The removed combined dish supplied both garlic and ssamjang.
        return (contents & ALL_INGREDIENTS) | ((contents & LEGACY_CONDIMENTS) != 0 ? GARLIC | SSAMJANG : 0);
    }

    public static int getModelIngredients(ItemStack stack) {
        int contents = getIngredients(stack);
        // shortcut: condiments and kimchi share the existing topping model until dedicated wrap models are added.
        return (contents & (MEAT | RICE)) | ((contents & (GARLIC | SSAMJANG | KIMCHI)) != 0 ? 4 : 0);
    }

    public static boolean canFold(ItemStack stack) {
        return getIngredients(stack) != 0;
    }

    public static FoodProperties getSsamFood(ItemStack leaf) {
        // A folded wrap already holds its final nutrition; never treat it as the base leaf again.
        if (leaf.getItem() instanceof SsamItem) {
            return leaf.getOrDefault(DataComponents.FOOD, ModFoodProperties.SSAM);
        }
        List<FoodProperties> foods = new ArrayList<>();
        FoodProperties leafFood = leaf.get(DataComponents.FOOD);
        foods.add(leafFood != null ? leafFood : ModFoodItems.LETTUCE.get().components().get(DataComponents.FOOD));
        int contents = getIngredients(leaf);
        for (int ingredient : INGREDIENT_ORDER) {
            if ((contents & ingredient) != 0) {
                Item food = switch (ingredient) {
                    case MEAT -> ModFoodItems.JEYUK_BOKKEUM.get();
                    case RICE -> ModFoodItems.COOKED_RICE_BOWL.get();
                    case GARLIC -> ModFoodItems.GARLIC_BOWL.get();
                    case SSAMJANG -> ModFoodItems.SSAMJANG_BOWL.get();
                    case KIMCHI -> ModFoodItems.KIMCHI_BOWL.get();
                    default -> throw new IllegalArgumentException("Unknown ssam ingredient: " + ingredient);
                };
                foods.add(food.components().get(DataComponents.FOOD));
            }
        }
        return ModFoodProperties.ssam(foods.toArray(FoodProperties[]::new));
    }

    public static ItemStack addIngredient(ItemStack leaf, int ingredient) {
        int contents = getIngredients(leaf);
        if (leaf.isEmpty() || Integer.bitCount(ingredient) != 1 || (ingredient & ALL_INGREDIENTS) == 0
                || (contents & ingredient) != 0) {
            return ItemStack.EMPTY;
        }
        ItemStack result = leaf.copyWithCount(1);
        CustomData.update(DataComponents.CUSTOM_DATA, result, tag -> tag.putInt(INGREDIENTS, contents | ingredient));
        return result;
    }

    static ItemStack toOpenWrap(ItemStack leaf, Item openItem) {
        ItemStack open = leaf.transmuteCopy(openItem, 1);
        // Default components are not part of transmuteCopy's patch; carry the base leaf's food explicitly.
        FoodProperties leafFood = leaf.get(DataComponents.FOOD);
        if (leafFood != null) {
            open.set(DataComponents.FOOD, leafFood);
        }
        return open;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (context.isSecondaryUseActive()) {
            return use(context.getLevel(), player, context.getHand()).getResult();
        }
        Level level = context.getLevel();
        BlockState source = level.getBlockState(context.getClickedPos());
        if (RiceBowlBlock.dishLeaf(source) != null && getIngredients(context.getItemInHand()) == 0
                && context.getHand() == InteractionHand.MAIN_HAND) {
            return RiceBowlBlock.takeLeaf(source, level, context.getClickedPos(), player);
        }
        int ingredient = ingredientFor(source);
        if (ingredient == 0 || !player.mayBuild()) {
            return InteractionResult.PASS;
        }
        ItemStack result = addIngredient(context.getItemInHand(), ingredient);
        if (result.isEmpty()) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.koreandelight.ssam.duplicate"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide) {
            BlockState remainder = ingredient == MEAT ? Blocks.AIR.defaultBlockState()
                    : ModBlocks.RICE_BOWL.get().defaultBlockState();
            if (!RiceBowlBlock.consumeServing(level, context.getClickedPos(), source, remainder)) {
                return InteractionResult.FAIL;
            }
            replaceOneInHand(player, context.getHand(), toOpenWrap(result, ModFoodItems.OPEN_SSAM.get()));
            level.playSound(null, context.getClickedPos(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, 1.2F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static int ingredientFor(BlockState state) {
        if (state.is(ModBlocks.JEYUK_BOKKEUM.get())) {
            return MEAT;
        }
        if (state.is(ModBlocks.COOKED_RICE_BOWL.get())) {
            return RICE;
        }
        if (state.is(ModBlocks.KIMCHI_BOWL.get())) {
            return KIMCHI;
        }
        if (state.is(ModBlocks.GARLIC_BOWL.get())) {
            return GARLIC;
        }
        return state.is(ModBlocks.SSAMJANG_BOWL.get()) ? SSAMJANG : 0;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack leaf = player.getItemInHand(hand);
        int contents = getIngredients(leaf);
        if (!player.isShiftKeyDown() && contents == 0) {
            return super.use(level, player, hand);
        }
        if (player.isShiftKeyDown() && canFold(leaf)) {
            if (!level.isClientSide) {
                ItemStack result = leaf.transmuteCopy(ModFoodItems.SSAM.get(), 1);
                result.set(DataComponents.FOOD, getSsamFood(leaf));
                replaceOneInHand(player, hand, result);
                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 0.6F, 1.1F);
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable(canFold(leaf)
                    ? "message.koreandelight.ssam.fold" : "message.koreandelight.ssam.missing"), true);
        }
        return InteractionResultHolder.fail(leaf);
    }

    private static void replaceOneInHand(Player player, InteractionHand hand, ItemStack result) {
        ItemStack remaining = player.getItemInHand(hand).copy();
        remaining.shrink(1);
        player.setItemInHand(hand, result);
        if (!remaining.isEmpty() && !player.getInventory().add(remaining)) {
            player.drop(remaining, false);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return getIngredients(stack) == 0 ? super.getName(stack) : Component.translatable("item.koreandelight.open_ssam");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.koreandelight.ssam.add").withStyle(ChatFormatting.YELLOW));
        appendIngredientsTooltip(stack, tooltip);
    }

    public static void appendIngredientsTooltip(ItemStack stack, List<Component> tooltip) {
        int contents = getIngredients(stack);
        if (contents == 0) {
            return;
        }
        int count = Integer.bitCount(contents);
        tooltip.add(Component.translatable("tooltip.koreandelight.ssam.ingredients", count).withStyle(ChatFormatting.GRAY));
        for (int ingredient : INGREDIENT_ORDER) {
            if ((contents & ingredient) != 0) {
                String name = switch (ingredient) {
                    case MEAT -> "jeyuk_bokkeum";
                    case RICE -> "cooked_rice_bowl";
                    case GARLIC -> "garlic";
                    case SSAMJANG -> "ssamjang";
                    case KIMCHI -> "kimchi";
                    default -> throw new IllegalArgumentException("Unknown ssam ingredient: " + ingredient);
                };
                tooltip.add(Component.literal("- ").append(Component.translatable("item.koreandelight." + name))
                        .withStyle(ChatFormatting.GRAY));
            }
        }
        if (count >= 3) {
            tooltip.add(Component.translatable("tooltip.koreandelight.ssam.variety_bonus").withStyle(ChatFormatting.GREEN));
        }
    }
}
