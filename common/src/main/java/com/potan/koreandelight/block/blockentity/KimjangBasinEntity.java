package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.recipe.KimjangInput;
import com.potan.koreandelight.recipe.KimjangRecipe;
import com.potan.koreandelight.recipe.ModRecipes;
import com.potan.koreandelight.tag.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class KimjangBasinEntity extends BlockEntity {
    private final SimpleContainer inventory = new SimpleContainer(5) {
        @Override
        public void setChanged() {
            super.setChanged();
            KimjangBasinEntity.this.setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    public KimjangBasinEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.KIMJANG_BASIN_ENTITY.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        NonNullList<ItemStack> list = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            list.set(i, inventory.getItem(i));
        }
        ContainerHelper.saveAllItems(tag, list, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        NonNullList<ItemStack> list = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, list, registries);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            inventory.setItem(i, list.get(i));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public boolean addIngredient(Player player, ItemStack stackInHand, Level level) {
        if (stackInHand.is(ModTags.KIMJANG_VEGETABLES)) {
            if (inventory.getItem(0).isEmpty()) {
                inventory.setItem(0, stackInHand.split(1));
                setChanged();
                return true;
            } else {
                player.displayClientMessage(Component.translatable("msg.koreandelight.already_has_vegetable"), true);
                return false;
            }
        }

        if (stackInHand.is(ModTags.KIMJANG_SEASONINGS)) {
            for (int i = 1; i < inventory.getContainerSize(); i++) {
                if (inventory.getItem(i).isEmpty()) {
                    inventory.setItem(i, stackInHand.split(1));
                    setChanged();
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    public boolean performKimjang(Player player, Level level) {
        ItemStack vegetable = inventory.getItem(0);
        if (vegetable.isEmpty()) {
            player.displayClientMessage(Component.translatable("msg.koreandelight.need_vegetable"), true);
            return false;
        }

        List<ItemStack> seasonings = new ArrayList<>();
        for (int i = 1; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty()) {
                seasonings.add(inventory.getItem(i));
            }
        }

        if (seasonings.isEmpty()) {
            player.displayClientMessage(Component.translatable("msg.koreandelight.need_spice"), true);
            return false;
        }

        KimjangInput input = new KimjangInput(vegetable, seasonings);
        
        for (var recipeHolder : level.getRecipeManager().getAllRecipesFor(ModRecipes.KIMJANG_RECIPE_TYPE.get())) {
            KimjangRecipe recipe = recipeHolder.value();
            if (recipe.matches(input, level)) {
                ItemStack result = recipe.assemble(input, level.registryAccess()).copy();
                if (!player.getInventory().add(result)) {
                    player.drop(result, false);
                }
                
                inventory.setItem(0, ItemStack.EMPTY);
                for (int i = 1; i < inventory.getContainerSize(); i++) {
                    inventory.setItem(i, ItemStack.EMPTY);
                }
                setChanged();
                return true;
            }
        }

        player.displayClientMessage(Component.translatable("msg.koreandelight.no_matching_recipe"), true);
        return false;
    }
}
