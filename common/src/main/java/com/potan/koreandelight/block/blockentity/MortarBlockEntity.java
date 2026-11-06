package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.item.ModItems;
import com.potan.koreandelight.item.ModFoodItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MortarBlockEntity extends BlockEntity {
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private int progress;
    private boolean finished;

    public MortarBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntityTypes.MORTAR.get(), pos, state); }
    public ItemStack getStack() { return items.get(0); }
    public boolean isFinished() { return finished; }

    public static ItemStack result(ItemStack input) {
        int count = input.getCount();
        if (input.is(ModItems.GARLIC.get())) return new ItemStack(ModItems.MINCED_GARLIC.get(), count / 2 * 3);
        if (input.is(ModFoodItems.RED_PEPPER.get())) return new ItemStack(ModItems.RED_PEPPER_POWDER.get(), count / 2 * 3);
        if (input.is(Items.SUGAR_CANE)) return new ItemStack(Items.SUGAR, count / 2 * 3);
        if (input.is(ModItems.BOILED_BEANS.get())) return new ItemStack(ModItems.MEJU_LUMP.get(), count);
        if (input.is(ModItems.STEAMED_RICE.get())) return new ItemStack(ModItems.RICE_FLOUR.get(), count);
        if (input.is(ModItems.STEAMED_GLUTINOUS_RICE.get())) return new ItemStack(ModItems.RICE_CAKE_DOUGH.get(), count);
        return ItemStack.EMPTY;
    }
    private static boolean bonus(ItemStack stack) {
        return stack.is(ModItems.GARLIC.get()) || stack.is(ModFoodItems.RED_PEPPER.get()) || stack.is(Items.SUGAR_CANE);
    }
    public int insert(ItemStack held) {
        ItemStack current = getStack();
        if (finished || (!current.isEmpty() && !ItemStack.isSameItemSameComponents(current, held))) return 0;
        if (result(held.copyWithCount(2)).isEmpty()) return 0;
        int count = Math.min(16 - current.getCount(), held.getCount());
        if (count <= 0) return 0;
        if (current.isEmpty()) items.set(0, held.copyWithCount(count)); else current.grow(count);
        progress = 0; sync(); return count;
    }
    public boolean strike(int amount) {
        ItemStack stack = getStack();
        if (finished || stack.isEmpty() || (bonus(stack) && stack.getCount() < 2)) return false;
        progress += amount;
        if (progress >= 8) {
            int remainder = bonus(stack) ? stack.getCount() % 2 : 0;
            ItemStack output = result(stack);
            if (remainder != 0 && level != null) net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.7, worldPosition.getZ() + 0.5, stack.copyWithCount(remainder));
            items.set(0, output); finished = true; progress = 0;
        }
        sync(); return true;
    }
    public ItemStack take() {
        ItemStack stack = getStack(); items.set(0, ItemStack.EMPTY); progress = 0; finished = false; sync(); return stack;
    }
    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries); ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("Progress", progress); tag.putBoolean("Finished", finished);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadItems(tag, items, registries);
        progress = tag.getInt("Progress"); finished = tag.getBoolean("Finished");
    }
    static void loadItems(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider registries) {
        items.clear();
        ContainerHelper.loadAllItems(tag, items, registries);
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveCustomOnly(registries); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
