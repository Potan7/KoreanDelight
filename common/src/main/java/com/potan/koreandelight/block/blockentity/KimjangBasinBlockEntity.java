package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.item.ModFoodItems;
import com.potan.koreandelight.item.ModItems;
import com.potan.koreandelight.item.ModFoodProperties;
import com.potan.koreandelight.mobeffect.ModEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class KimjangBasinBlockEntity extends BlockEntity {
    public static final int BATCH_SIZE = 2;
    public static final int MAX_BATCHES = 8;
    public static final int SEASONING_COUNT = 2;
    public static final int MAX_SPICY_LEVEL = 3;
    public static final int MAX_SPICY_SECONDS = 60;
    public static final int MIXES = 3;
    private static final int CABBAGE = 0;
    private static final int PEPPER = 2;
    private static final int GARLIC = 3;

    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private int mixes;

    public KimjangBasinBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.KIMJANG_BASIN.get(), pos, state);
    }

    public ItemStack cabbage() {
        return items.get(CABBAGE);
    }

    public void insert(Player player, InteractionHand hand, ItemStack held) {
        if (cabbage().is(ModFoodItems.KIMCHI.get())) {
            showStatus(player);
            return;
        }
        if (held.is(ModFoodItems.KIMCHI_CABBAGE.get())) {
            add(player, held, CABBAGE, BATCH_SIZE * MAX_BATCHES);
        } else if (held.is(ModItems.RED_PEPPER_POWDER.get())) {
            add(player, held, PEPPER, SEASONING_COUNT * MAX_BATCHES * MAX_SPICY_LEVEL);
        } else if (held.is(ModItems.MINCED_GARLIC.get())) {
            add(player, held, GARLIC, SEASONING_COUNT * MAX_BATCHES * MAX_SPICY_SECONDS / 10);
        }
        showStatus(player);
    }

    private void add(Player player, ItemStack held, int slot, int limit) {
        ItemStack stored = items.get(slot);
        if (!stored.isEmpty() && !ItemStack.isSameItemSameComponents(stored, held)) {
            return;
        }
        int count = Math.min(held.getCount(), Math.min(limit, held.getMaxStackSize()) - stored.getCount());
        if (count > 0) {
            items.set(slot, held.copyWithCount(stored.getCount() + count));
            if (!player.isCreative()) {
                held.shrink(count);
            }
            mixes = 0;
            sync();
        }
    }

    public void use(Player player) {
        if (player.isShiftKeyDown() || cabbage().is(ModFoodItems.KIMCHI.get())) {
            for (ItemStack stack : takeContents()) {
                if (!stack.isEmpty() && !player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
            }
            sync();
            showStatus(player);
        } else if (readyToMix()) {
            mixes++;
            if (mixes == MIXES) {
                int outputCount = cabbage().getCount();
                int batches = outputCount / BATCH_SIZE;
                int spicyLevel = Math.min(MAX_SPICY_LEVEL, items.get(PEPPER).getCount() / (SEASONING_COUNT * batches));
                int duration = Math.min(MAX_SPICY_SECONDS, items.get(GARLIC).getCount() / (SEASONING_COUNT * batches) * 10);
                ItemStack result = new ItemStack(ModFoodItems.KIMCHI.get(), outputCount);
                FoodProperties food = ModFoodProperties.KIMCHI_FOOD;
                result.set(DataComponents.FOOD, new FoodProperties(food.nutrition(), food.saturation(), food.canAlwaysEat(),
                        food.eatSeconds(), food.usingConvertsTo(),
                        List.of(new FoodProperties.PossibleEffect(() -> new MobEffectInstance(ModEffects.SPICY_HOLDER,
                                duration * 20, spicyLevel - 1), 1.0F))));
                items.clear();
                items.set(CABBAGE, result);
                mixes = 0;
            }
            play(SoundEvents.MUD_STEP);
            sync();
            showStatus(player);
        } else {
            showStatus(player);
        }
    }

    private boolean readyToMix() {
        return cabbage().is(ModFoodItems.KIMCHI_CABBAGE.get()) && cabbage().getCount() > 0
                && cabbage().getCount() % BATCH_SIZE == 0
                && items.get(PEPPER).getCount() >= cabbage().getCount() / BATCH_SIZE * SEASONING_COUNT
                && items.get(PEPPER).getCount() % (cabbage().getCount() / BATCH_SIZE * SEASONING_COUNT) == 0
                && items.get(PEPPER).getCount() <= cabbage().getCount() / BATCH_SIZE * SEASONING_COUNT * MAX_SPICY_LEVEL
                && items.get(GARLIC).getCount() >= cabbage().getCount() / BATCH_SIZE * SEASONING_COUNT
                && items.get(GARLIC).getCount() % (cabbage().getCount() / BATCH_SIZE * SEASONING_COUNT) == 0
                && items.get(GARLIC).getCount() <= cabbage().getCount() / BATCH_SIZE * SEASONING_COUNT * MAX_SPICY_SECONDS / 10;
    }

    public NonNullList<ItemStack> takeContents() {
        NonNullList<ItemStack> contents = NonNullList.create();
        contents.addAll(items);
        items.clear();
        mixes = 0;
        return contents;
    }

    private void showStatus(Player player) {
        String prefix = "block.koreandelight.kimjang_basin.";
        Component status;
        if (cabbage().is(ModFoodItems.KIMCHI.get())) {
            status = Component.translatable(prefix + "finished");
        } else if (readyToMix()) {
            status = Component.translatable(prefix + "mix", mixes, MIXES);
        } else {
            status = Component.translatable(prefix + "ratio", cabbage().getCount(),
                    items.get(PEPPER).getCount(), items.get(GARLIC).getCount());
        }
        player.displayClientMessage(status, true);
    }

    private void play(SoundEvent sound) {
        level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, 0.7F, 1.0F);
    }

    private void sync() {
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("mixes", mixes);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items.clear();
        ContainerHelper.loadAllItems(tag, items, registries);
        mixes = tag.getInt("mixes");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
