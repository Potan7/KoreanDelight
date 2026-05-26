package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.recipe.FermentationInput;
import com.potan.koreandelight.recipe.FermentationRecipe;
import com.potan.koreandelight.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class OnggiBlockEntity extends BlockEntity {
    private final SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            OnggiBlockEntity.this.setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    private int agingProgress = 0;
    private int maxProgress = 0;

    public OnggiBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OnggiBlockEntity blockEntity) {
        if (level.isClientSide) {
            blockEntity.spawnParticles(level, pos);
            return;
        }

        ItemStack inputStack = blockEntity.inventory.getItem(0);
        if (inputStack.isEmpty()) {
            if (blockEntity.agingProgress > 0) {
                blockEntity.agingProgress = 0;
                blockEntity.setChanged();
            }
            return;
        }

        FermentationInput input = new FermentationInput(inputStack);
        Optional<FermentationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.FERMENTATION_RECIPE_TYPE.get(), input, level)
                .map(holder -> holder.value());

        if (recipe.isPresent()) {
            FermentationRecipe fermentationRecipe = recipe.get();
            blockEntity.maxProgress = fermentationRecipe.getFermentation_time();
            blockEntity.agingProgress++;
            
            if (blockEntity.agingProgress % 20 == 0) {
                blockEntity.setChanged();
            }

            if (blockEntity.agingProgress >= fermentationRecipe.getFermentation_time()) {
                ItemStack result = fermentationRecipe.assemble(input, level.registryAccess()).copy();
                result.setCount(inputStack.getCount());
                blockEntity.inventory.setItem(0, result);
                blockEntity.agingProgress = 0;
                blockEntity.setChanged();
            }
        } else {
            if (blockEntity.agingProgress > 0) {
                blockEntity.agingProgress = 0;
                blockEntity.setChanged();
            }
        }
    }

    private void spawnParticles(Level level, BlockPos pos) {
        if (agingProgress <= 0 || maxProgress <= 0) return;
        double progressRatio = (double) agingProgress / maxProgress;
        RandomSource random = level.getRandom();

        if (random.nextFloat() < 0.2F + (progressRatio * 0.3F)) {
            double x = pos.getX() + 0.3D + random.nextDouble() * 0.4D;
            double y = pos.getY() + 0.5D + random.nextDouble() * 0.5D;
            double z = pos.getZ() + 0.3D + random.nextDouble() * 0.4D;

            if (progressRatio < 0.5) {
                level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, x, y, z, 0.0D, 0.02D, 0.0D);
            } else if (progressRatio < 0.9) {
                level.addParticle(net.minecraft.core.particles.ParticleTypes.EFFECT, x, y, z, 0.0D, 0.05D, 0.0D);
            } else {
                level.addParticle(net.minecraft.core.particles.ParticleTypes.BUBBLE_POP, x, y, z, 0.0D, 0.08D, 0.0D);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("aging_progress", this.agingProgress);
        tag.putInt("max_progress", this.maxProgress);
        NonNullList<ItemStack> list = NonNullList.withSize(1, ItemStack.EMPTY);
        list.set(0, inventory.getItem(0));
        ContainerHelper.saveAllItems(tag, list, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.agingProgress = tag.getInt("aging_progress");
        this.maxProgress = tag.getInt("max_progress");
        NonNullList<ItemStack> list = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, list, registries);
        inventory.setItem(0, list.get(0));
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
}
