package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.recipe.FermentationRecipe;
import com.potan.koreandelight.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

import java.util.Optional;

public class OnggiBlockEntity extends SyncedBlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();   // 저장 필요함을 마인크래프트에 알림
            inventoryChanged(); // SyncedBlockEntity의 클라이언트 동기화 메서드
            agingProgress = 0;  // 일단 아이템 변경 시 진행도 초기화
        }
    };

    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(() -> inventory);
    private int agingProgress = 0;

    public OnggiBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OnggiBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }

        ItemStack inputStack = blockEntity.inventory.getStackInSlot(0);
        if (inputStack.isEmpty()) {
            blockEntity.agingProgress = 0;
            return;
        }
        SimpleContainer container = new SimpleContainer(inputStack);

        Optional<FermentationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.FERMENTATION_RECIPE_TYPE.get(), container, level);

        if (recipe.isPresent()) {
            FermentationRecipe jarRecipe = recipe.get();
            blockEntity.agingProgress++;

            // 숙성 완료
            if (blockEntity.agingProgress >= jarRecipe.getFermentation_time()) {
                ItemStack result = jarRecipe.getResultItem(level.registryAccess()).copy();

                // 결과물로 교체
                blockEntity.inventory.setStackInSlot(0, result);
                blockEntity.agingProgress = 0;

                // (선택) 소리나 파티클 효과 추가
                // level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        } else {
            blockEntity.agingProgress = 0;
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    // 데이터를 저장할 때 (서버 -> NBT)
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("aging_progress", this.agingProgress);
        tag.put("inventory", inventory.serializeNBT());
    }

    // 데이터를 불러올 때 (NBT -> 객체)
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.agingProgress = tag.getInt("aging_progress");
        inventory.deserializeNBT(tag.getCompound("inventory"));
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryOptional.invalidate();
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }
}
