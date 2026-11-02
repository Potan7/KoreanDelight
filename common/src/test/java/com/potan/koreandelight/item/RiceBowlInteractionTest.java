package com.potan.koreandelight.item;

import com.potan.koreandelight.block.blockentity.RiceBowlBlockEntity;
import com.potan.koreandelight.block.custom.RiceBowlBlock;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RiceBowlInteractionTest {
    public static void main(String[] args) {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        MappedRegistry<Item> items = (MappedRegistry<Item>) BuiltInRegistries.ITEM;
        items.unfreeze();
        TestBowl bowl = new TestBowl();
        Registry.register(items, ResourceLocation.fromNamespaceAndPath("koreandelight_test", "rice_bowl"), bowl);
        SsamItem ssam = Registry.register(items, ResourceLocation.fromNamespaceAndPath("koreandelight_test", "ssam"),
                new SsamItem(new Item.Properties().food(ModFoodProperties.SSAM)));
        items.freeze();

        // Clicking terrain normally must start eating, rather than attempt placement.
        assert bowl.useOn(context(false, Direction.UP)) == InteractionResult.CONSUME_PARTIAL;
        assert bowl.foodUses == 1;
        assert bowl.useOn(context(false, Direction.NORTH)) == InteractionResult.CONSUME_PARTIAL;
        assert bowl.foodUses == 2;

        // Sneaking against walls or ceilings must neither place nor eat the bowl.
        for (Direction face : Direction.values()) {
            if (face != Direction.UP) {
                assert bowl.useOn(context(true, face)) == InteractionResult.FAIL;
            }
        }
        assert bowl.foodUses == 2;

        // Being full or holding an empty bowl must not fall back to placement.
        bowl.foodResult = InteractionResult.FAIL;
        assert bowl.useOn(context(false, Direction.UP)) == InteractionResult.FAIL;
        bowl.foodResult = InteractionResult.PASS;
        assert bowl.useOn(context(false, Direction.UP)) == InteractionResult.PASS;
        testPlacementOffsets();
        testLargeDishPlacement();
        testFoodContainer();
        testSsamNutrition();
        testFinalSsamFood(ssam);
        testSsamTooltip(ssam);
        testSsamIngredients();
        testLeafNutritionPreserved();
        testLeafDishStacking();
        testOptionalIngredients(new ItemStack(net.minecraft.world.item.Items.KELP), 0);
        testServingState();
        System.out.println("Rice bowl interaction checks passed");
    }

    private static void testFoodContainer() {
        var original = new net.minecraft.world.food.FoodProperties.Builder().nutrition(6).saturationModifier(0.6F)
                .effect(() -> new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 200), 1.0F).build();
        var served = ModFoodProperties.servedIn(original, net.minecraft.world.item.Items.BOWL);
        assert served.nutrition() == original.nutrition();
        assert served.saturation() == original.saturation();
        assert served.eatDurationTicks() == original.eatDurationTicks();
        assert served.effects().getFirst().effect().getDuration() == 200;
        assert served.usingConvertsTo().orElseThrow().is(net.minecraft.world.item.Items.BOWL);
        assert original.usingConvertsTo().isEmpty();
    }

    private static void testLeafDishStacking() {
        assert RiceBowlBlock.canTakeLeaf(ItemStack.EMPTY, Items.KELP);
        assert RiceBowlBlock.canTakeLeaf(new ItemStack(Items.KELP, 63), Items.KELP);
        assert !RiceBowlBlock.canTakeLeaf(new ItemStack(Items.KELP, 64), Items.KELP);
        assert !RiceBowlBlock.canTakeLeaf(new ItemStack(Items.APPLE), Items.KELP);
        assert !RiceBowlBlock.canTakeLeaf(new ItemStack(Items.KELP), Items.APPLE);
        ItemStack assembled = SsamLeafItem.addIngredient(new ItemStack(Items.KELP), SsamLeafItem.MEAT);
        assert !RiceBowlBlock.canTakeLeaf(assembled, Items.KELP);
    }

    private static void testLeafNutritionPreserved() {
        var leafFood = new net.minecraft.world.food.FoodProperties.Builder().nutrition(3).saturationModifier(0.2F).build();
        for (boolean customFood : new boolean[]{false, true}) {
            ItemStack leaf = new ItemStack(Items.APPLE, 4);
            if (customFood) {
                leaf.set(DataComponents.FOOD, leafFood);
            }
            var expectedFood = leaf.get(DataComponents.FOOD);
            ItemStack assembled = SsamLeafItem.addIngredient(leaf, SsamLeafItem.MEAT);
            ItemStack open = SsamLeafItem.toOpenWrap(assembled, Items.STICK);
            assert open.get(DataComponents.FOOD).equals(expectedFood);
            assert open.getCount() == 1 && leaf.getCount() == 4;
            assert SsamLeafItem.getIngredients(open) == SsamLeafItem.MEAT;
            ItemStack withRice = SsamLeafItem.addIngredient(open, SsamLeafItem.RICE);
            assert withRice.get(DataComponents.FOOD).equals(expectedFood);
            assert SsamLeafItem.getIngredients(withRice) == (SsamLeafItem.MEAT | SsamLeafItem.RICE);
            assert SsamLeafItem.getIngredients(leaf) == 0;
        }
    }

    private static void testSsamIngredients() {
        int[][] orders = {{1, 2, 16, 32}, {32, 16, 2, 1}, {16, 1, 32, 2}, {2, 32, 1, 16}};
        for (int[] order : orders) {
            ItemStack original = new ItemStack(net.minecraft.world.item.Items.KELP, 64);
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    original, tag -> tag.putString("OtherData", "preserved"));
            ItemStack leaf = original;
            int expected = 0;
            for (int ingredient : order) {
                leaf = SsamLeafItem.addIngredient(leaf, ingredient);
                expected |= ingredient;
                assert !leaf.isEmpty() && leaf.getCount() == 1;
                assert SsamLeafItem.getIngredients(leaf) == expected;
                assert SsamLeafItem.addIngredient(leaf, ingredient).isEmpty();
            }
            assert SsamLeafItem.getIngredients(leaf) == SsamLeafItem.COMPLETE;
            assert SsamLeafItem.getIngredients(original) == 0 && original.getCount() == 64;
            var data = leaf.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
            assert data.copyTag().getString("OtherData").equals("preserved");
            var encoded = net.minecraft.world.item.component.CustomData.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, data).getOrThrow();
            var decoded = net.minecraft.world.item.component.CustomData.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, encoded).getOrThrow();
            ItemStack restored = new ItemStack(net.minecraft.world.item.Items.KELP);
            restored.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, decoded);
            assert SsamLeafItem.getIngredients(restored) == SsamLeafItem.COMPLETE;
        }
        assert SsamLeafItem.addIngredient(ItemStack.EMPTY, SsamLeafItem.MEAT).isEmpty();
        assert SsamLeafItem.addIngredient(new ItemStack(net.minecraft.world.item.Items.KELP), 3).isEmpty();
        assert SsamLeafItem.addIngredient(new ItemStack(net.minecraft.world.item.Items.KELP), 64).isEmpty();
        assert SsamLeafItem.addIngredient(new ItemStack(net.minecraft.world.item.Items.KELP), 0).isEmpty();
        assert SsamLeafItem.addIngredient(new ItemStack(net.minecraft.world.item.Items.KELP), 4).isEmpty();
        ItemStack garlic = SsamLeafItem.addIngredient(new ItemStack(net.minecraft.world.item.Items.KELP), SsamLeafItem.GARLIC);
        assert SsamLeafItem.getIngredients(garlic) == SsamLeafItem.GARLIC;
        assert SsamLeafItem.addIngredient(garlic, SsamLeafItem.GARLIC).isEmpty();
        ItemStack both = SsamLeafItem.addIngredient(garlic, SsamLeafItem.SSAMJANG);
        assert SsamLeafItem.getIngredients(both) == (SsamLeafItem.GARLIC | SsamLeafItem.SSAMJANG);
        assert SsamLeafItem.getIngredients(garlic) == SsamLeafItem.GARLIC;
        assert SsamLeafItem.getModelIngredients(garlic) == 4;
        assert SsamLeafItem.getModelIngredients(both) == 4;
        for (int legacy : new int[]{4, 4 | 16, 4 | 32, 4 | 16 | 32}) {
            ItemStack leaf = new ItemStack(net.minecraft.world.item.Items.KELP);
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    leaf, tag -> tag.putInt("SsamIngredients", legacy | SsamLeafItem.RICE));
            assert SsamLeafItem.getIngredients(leaf) == (SsamLeafItem.RICE | SsamLeafItem.GARLIC | SsamLeafItem.SSAMJANG);
            assert SsamLeafItem.addIngredient(leaf, SsamLeafItem.GARLIC).isEmpty();
            assert SsamLeafItem.addIngredient(leaf, SsamLeafItem.SSAMJANG).isEmpty();
        }
    }

    private static void testSsamNutrition() {
        var lettuce = new net.minecraft.world.food.FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build();
        var meat = new net.minecraft.world.food.FoodProperties.Builder().nutrition(14).saturationModifier(0.9F).build();
        var rice = new net.minecraft.world.food.FoodProperties.Builder().nutrition(6).saturationModifier(0.4F)
                .usingConvertsTo(net.minecraft.world.item.Items.BOWL).build();
        for (var fillings : new net.minecraft.world.food.FoodProperties[][]{
                {lettuce}, {lettuce, meat}, {lettuce, rice}, {lettuce, rice, meat}, {meat, lettuce, rice},
                {lettuce, ModFoodProperties.GARLIC}, {lettuce, ModFoodProperties.SSAMJANG},
                {lettuce, ModFoodProperties.GARLIC, ModFoodProperties.SSAMJANG},
                {lettuce, rice, meat, ModFoodProperties.GARLIC},
                {lettuce, rice, meat, ModFoodProperties.GARLIC, ModFoodProperties.SSAMJANG}}) {
            var wrap = ModFoodProperties.ssam(fillings);
            int nutrition = 0;
            float saturation = 0;
            for (var food : fillings) {
                nutrition += food.nutrition();
                saturation += food.saturation();
            }
            if (fillings.length >= 4) {
                saturation *= 1.2F;
            }
            assert wrap.nutrition() == nutrition;
            assert Math.abs(wrap.saturation() - saturation) < 0.00001F;
            assert wrap.usingConvertsTo().isEmpty();
            var codec = net.minecraft.core.component.DataComponents.FOOD.codec();
            var encoded = codec.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, wrap).getOrThrow();
            var restored = codec.parse(net.minecraft.nbt.NbtOps.INSTANCE, encoded).getOrThrow();
            assert restored.nutrition() == nutrition;
            assert Math.abs(restored.saturation() - saturation) < 0.00001F;
        }
        // Saturation is already in points; applying saturationModifier again would multiply it twice.
        var wrap = ModFoodProperties.ssam(lettuce, rice, meat);
        assert wrap.nutrition() == 22;
        assert Math.abs(wrap.saturation() - 31.2F) < 0.00001F;
        var varied = ModFoodProperties.ssam(lettuce, rice, meat, ModFoodProperties.GARLIC);
        assert varied.nutrition() == 24;
        assert Math.abs(varied.saturation() - 38.4F) < 0.00001F;
    }

    private static void testFinalSsamFood(SsamItem ssam) {
        var spicy = new net.minecraft.world.food.FoodProperties.Builder().nutrition(6).saturationModifier(0.6F)
                .effect(() -> new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 200, 1), 0.75F).build();
        var expected = ModFoodProperties.ssam(ModFoodProperties.PERILLA_LEAF, spicy,
                ModFoodProperties.GARLIC, ModFoodProperties.SSAMJANG);
        assert expected.nutrition() == 12;
        assert Math.abs(expected.saturation() - 12.48F) < 0.00001F;
        assert expected.effects().size() == 1;
        assert expected.effects().getFirst().effect().getDuration() == 200;
        assert expected.effects().getFirst().effect().getAmplifier() == 1;
        assert expected.effects().getFirst().probability() == 0.75F;
        assert expected.usingConvertsTo().isEmpty();
        ItemStack folded = new ItemStack(ssam);
        net.minecraft.world.item.component.CustomData.update(DataComponents.CUSTOM_DATA, folded,
                tag -> tag.putInt("SsamIngredients", SsamLeafItem.KIMCHI | SsamLeafItem.GARLIC | SsamLeafItem.SSAMJANG));
        folded.set(DataComponents.FOOD, expected);
        for (int attempt = 0; attempt < 20; attempt++) {
            folded.set(DataComponents.FOOD, SsamLeafItem.getSsamFood(folded));
            assert folded.get(DataComponents.FOOD).equals(expected);
            folded = folded.copy();
        }
    }

    private static void testSsamTooltip(SsamItem ssam) {
        var tooltip = new java.util.ArrayList<net.minecraft.network.chat.Component>();
        SsamLeafItem.appendIngredientsTooltip(new ItemStack(Items.KELP), tooltip);
        assert tooltip.isEmpty();
        ItemStack all = new ItemStack(Items.KELP);
        for (int ingredient : new int[]{SsamLeafItem.MEAT, SsamLeafItem.RICE, SsamLeafItem.GARLIC,
                SsamLeafItem.SSAMJANG, SsamLeafItem.KIMCHI}) {
            all = SsamLeafItem.addIngredient(all, ingredient);
        }
        SsamLeafItem.appendIngredientsTooltip(all, tooltip);
        assert tooltip.size() == 7;
        String[] names = {"jeyuk_bokkeum", "cooked_rice_bowl", "garlic", "ssamjang", "kimchi"};
        for (int i = 0; i < names.length; i++) {
            var contents = (net.minecraft.network.chat.contents.TranslatableContents)
                    tooltip.get(i + 1).getSiblings().getFirst().getContents();
            assert contents.getKey().equals("item.koreandelight." + names[i]);
        }
        var finishedTooltip = new java.util.ArrayList<net.minecraft.network.chat.Component>();
        ssam.appendHoverText(all.transmuteCopy(ssam, 1), null, finishedTooltip, net.minecraft.world.item.TooltipFlag.NORMAL);
        assert finishedTooltip.equals(tooltip);
        ItemStack two = SsamLeafItem.addIngredient(new ItemStack(Items.KELP), SsamLeafItem.MEAT);
        two = SsamLeafItem.addIngredient(two, SsamLeafItem.RICE);
        tooltip.clear();
        SsamLeafItem.appendIngredientsTooltip(two, tooltip);
        assert tooltip.size() == 3;
    }

    private static void testOptionalIngredients(ItemStack leaf, int contents) {
        assert SsamLeafItem.canFold(leaf) == (contents != 0);
        assert SsamLeafItem.getIngredients(leaf) == contents;
        assert SsamLeafItem.getModelIngredients(leaf) >= 0 && SsamLeafItem.getModelIngredients(leaf) <= 7;
        for (int ingredient : new int[]{SsamLeafItem.MEAT, SsamLeafItem.RICE, SsamLeafItem.GARLIC,
                SsamLeafItem.SSAMJANG, SsamLeafItem.KIMCHI}) {
            ItemStack next = SsamLeafItem.addIngredient(leaf, ingredient);
            if ((contents & ingredient) != 0) {
                assert next.isEmpty();
            } else {
                testOptionalIngredients(next, contents | ingredient);
                assert SsamLeafItem.getIngredients(leaf) == contents;
            }
        }
    }

    private static void testServingState() {
        MappedRegistry<net.minecraft.world.level.block.Block> blocks =
                (MappedRegistry<net.minecraft.world.level.block.Block>) BuiltInRegistries.BLOCK;
        blocks.unfreeze();
        TestDish dish = new TestDish();
        Registry.register(blocks, ResourceLocation.fromNamespaceAndPath("koreandelight_test", "dish"), dish);
        blocks.freeze();
        MappedRegistry<Item> items = (MappedRegistry<Item>) BuiltInRegistries.ITEM;
        items.unfreeze();
        RiceBowlItem dishItem = new RiceBowlItem(dish, new Item.Properties()
                .craftRemainder(net.minecraft.world.item.Items.BOWL));
        Registry.register(items, ResourceLocation.fromNamespaceAndPath("koreandelight_test", "dish"), dishItem);
        dishItem.registerBlocks(Item.BY_BLOCK, dishItem);
        items.freeze();
        assert dish.defaultBlockState().getValue(RiceBowlBlock.SERVINGS) == 5;
        var portion = dish.defaultBlockState();
        for (int remaining = 5; remaining > 0; remaining--) {
            assert portion.getValue(RiceBowlBlock.SERVINGS) == remaining;
            portion = RiceBowlBlock.afterServing(portion, Blocks.AIR.defaultBlockState());
        }
        assert portion.isAir();
        assert RiceBowlBlock.afterServing(dish.defaultBlockState().setValue(RiceBowlBlock.SERVINGS, 1),
                Blocks.FLOWER_POT.defaultBlockState()).is(Blocks.FLOWER_POT);
        for (int remaining = 1; remaining <= RiceBowlBlock.DISH_SERVINGS; remaining++) {
            var state = dish.defaultBlockState().setValue(RiceBowlBlock.SERVINGS, remaining);
            ItemStack pickedUp = RiceBowlBlock.pickupStack(state);
            assert RiceBowlBlock.canPickup(state) == (remaining == RiceBowlBlock.DISH_SERVINGS);
            if (remaining < RiceBowlBlock.DISH_SERVINGS) {
                assert pickedUp.isEmpty();
                var drops = dish.usedDrops(state);
                assert drops.size() == 1 && drops.getFirst().is(net.minecraft.world.item.Items.BOWL);
                continue;
            }
            assert pickedUp.is(dishItem);
            var properties = pickedUp.get(net.minecraft.core.component.DataComponents.BLOCK_STATE);
            var encoded = net.minecraft.world.item.component.BlockItemStateProperties.CODEC
                    .encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, properties).getOrThrow();
            var decoded = net.minecraft.world.item.component.BlockItemStateProperties.CODEC
                    .parse(net.minecraft.nbt.NbtOps.INSTANCE, encoded).getOrThrow();
            assert decoded.apply(dish.defaultBlockState()).getValue(RiceBowlBlock.SERVINGS) == remaining;
        }
    }

    private static class TestDish extends RiceBowlBlock {
        private TestDish() {
            super(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of(), DEFAULT_SHAPE, DISH_SERVINGS);
        }

        private java.util.List<ItemStack> usedDrops(net.minecraft.world.level.block.state.BlockState state) {
            return getDrops(state, null);
        }
    }

    private static void testPlacementOffsets() {
        BlockPos pos = new BlockPos(10, 64, -8);
        assertOffset(pos, new Vec3(10.5, 64, -7.5), 0, 0);
        assertOffset(pos, new Vec3(10.65, 64, -7.6), 0.15, -0.1);
        assertOffset(pos, new Vec3(10.35, 64, -7.4), -0.15, 0.1);
        assertOffset(pos, new Vec3(10, 64, -8), -0.3, -0.3);
        assertOffset(pos, new Vec3(11, 64, -7), 0.3, 0.3);
        assertOffset(new BlockPos(-10, 90, 8), new Vec3(-9.35, 90, 8.4), 0.15, -0.1);
        assertOffset(pos, new Vec3(Double.NaN, 64, Double.POSITIVE_INFINITY), 0, 0);

        // Even when aiming at an edge, the entire bowl must remain inside its block cell.
        for (double x : new double[]{0, 0.15, 0.4, 0.5, 0.6, 0.85, 1}) {
            Vec3 offset = RiceBowlBlockEntity.offsetFromClick(BlockPos.ZERO, new Vec3(x, 1, 1 - x));
            assert 0.3 + offset.x >= -1.0E-9 && 0.7 + offset.x <= 1 + 1.0E-9;
            assert 0.3 + offset.z >= -1.0E-9 && 0.7 + offset.z <= 1 + 1.0E-9;
        }
    }

    private static void testLargeDishPlacement() {
        // Platters and individual bowls must stay in their cell even when placed at a corner.
        for (var shape : new net.minecraft.world.phys.shapes.VoxelShape[]{
                RiceBowlBlock.DEFAULT_SHAPE,
                net.minecraft.world.level.block.Block.box(2, 0, 2, 14, 4, 14),
                net.minecraft.world.level.block.Block.box(1, 0, 1.5, 15, 7, 14.5)}) {
            for (double x : new double[]{0, 0.2, 0.5, 0.8, 1}) {
                Vec3 offset = RiceBowlBlockEntity.offsetFromClick(BlockPos.ZERO, new Vec3(x, 1, 1 - x), shape);
                assert shape.min(Direction.Axis.X) + offset.x >= -1.0E-9;
                assert shape.max(Direction.Axis.X) + offset.x <= 1 + 1.0E-9;
                assert shape.min(Direction.Axis.Z) + offset.z >= -1.0E-9;
                assert shape.max(Direction.Axis.Z) + offset.z <= 1 + 1.0E-9;
            }
            assert RiceBowlBlockEntity.offsetFromClick(BlockPos.ZERO,
                    new Vec3(Double.NaN, 1, Double.POSITIVE_INFINITY), shape).equals(Vec3.ZERO);
        }
    }

    private static void assertOffset(BlockPos pos, Vec3 hit, double x, double z) {
        Vec3 actual = RiceBowlBlockEntity.offsetFromClick(pos, hit);
        assert Math.abs(actual.x - x) < 1.0E-9;
        assert actual.y == 0;
        assert Math.abs(actual.z - z) < 1.0E-9;
    }

    private static UseOnContext context(boolean sneaking, Direction face) {
        return new UseOnContext(null, null, InteractionHand.MAIN_HAND, ItemStack.EMPTY,
                new BlockHitResult(Vec3.ZERO, face, BlockPos.ZERO, false)) {
            @Override
            public boolean isSecondaryUseActive() {
                return sneaking;
            }
        };
    }

    private static class TestBowl extends RiceBowlItem {
        private int foodUses;
        private InteractionResult foodResult = InteractionResult.CONSUME;

        private TestBowl() {
            super(Blocks.FLOWER_POT, new Properties());
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            foodUses++;
            return new InteractionResultHolder<>(foodResult, ItemStack.EMPTY);
        }
    }
}
