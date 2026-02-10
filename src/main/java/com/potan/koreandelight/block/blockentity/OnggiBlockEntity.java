package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.recipe.FermentationRecipe;
import com.potan.koreandelight.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
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

    private final FluidTank fluidTank = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            inventoryChanged();
        }
    };

    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(() -> inventory);
    private final LazyOptional<IFluidHandler> fluidOptional = LazyOptional.of(() -> fluidTank);
    private int agingProgress = 0;
    private int maxProgress = 0; // 클라이언트 파티클 계산을 위해 최대 진행도 저장

    public OnggiBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OnggiBlockEntity blockEntity) {
        if (level.isClientSide) {
            blockEntity.spawnParticles(level, pos);
            return;
        }

        ItemStack inputStack = blockEntity.inventory.getStackInSlot(0);
        if (inputStack.isEmpty()) {
            if (blockEntity.agingProgress > 0) {
                blockEntity.agingProgress = 0;
                blockEntity.inventoryChanged();
            }
            return;
        }
        SimpleContainer container = new SimpleContainer(inputStack);

        Optional<FermentationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.FERMENTATION_RECIPE_TYPE.get(), container, level);

        if (recipe.isPresent()) {
            FermentationRecipe fermentationRecipe = recipe.get();
            blockEntity.maxProgress = fermentationRecipe.getFermentation_time();

            // 액체 조건 확인 (입력)
            if (!fermentationRecipe.getFluid().isEmpty()) {
                if (blockEntity.fluidTank.getFluidAmount() < fermentationRecipe.getFluid().getAmount() ||
                    !blockEntity.fluidTank.getFluid().getFluid().isSame(fermentationRecipe.getFluid().getFluid())) {
                    if (blockEntity.agingProgress > 0) {
                        blockEntity.agingProgress = 0;
                        blockEntity.inventoryChanged();
                    }
                    return;
                }
            }

            // 액체 조건 확인 (출력)
            if (!fermentationRecipe.getFluidResult().isEmpty()) {
                int currentAmount = blockEntity.fluidTank.getFluidAmount();
                int inputAmount = fermentationRecipe.getFluid().isEmpty() ? 0 : fermentationRecipe.getFluid().getAmount();
                int outputAmount = fermentationRecipe.getFluidResult().getAmount();
                int remainingAmount = currentAmount - inputAmount;

                if (remainingAmount + outputAmount > blockEntity.fluidTank.getCapacity()) {
                    if (blockEntity.agingProgress > 0) {
                        blockEntity.agingProgress = 0;
                        blockEntity.inventoryChanged();
                    }
                    return;
                }

                if (remainingAmount > 0 && !blockEntity.fluidTank.getFluid().getFluid().isSame(fermentationRecipe.getFluidResult().getFluid())) {
                    if (blockEntity.agingProgress > 0) {
                        blockEntity.agingProgress = 0;
                        blockEntity.inventoryChanged();
                    }
                    return;
                }
            }

            blockEntity.agingProgress++;
            
            // 20틱(1초)마다 클라이언트에 진행도 동기화 (성능 고려)
            if (blockEntity.agingProgress % 20 == 0) {
                blockEntity.inventoryChanged();
            }

            // 숙성 완료
            if (blockEntity.agingProgress >= fermentationRecipe.getFermentation_time()) {
                ItemStack result = fermentationRecipe.getResultItem(level.registryAccess()).copy();
                result.setCount(inputStack.getCount());

                if (!fermentationRecipe.getFluid().isEmpty()) {
                    blockEntity.fluidTank.drain(fermentationRecipe.getFluid(), IFluidHandler.FluidAction.EXECUTE);
                }

                if (!fermentationRecipe.getFluidResult().isEmpty()) {
                    blockEntity.fluidTank.fill(fermentationRecipe.getFluidResult(), IFluidHandler.FluidAction.EXECUTE);
                }

                blockEntity.inventory.setStackInSlot(0, result);
                blockEntity.agingProgress = 0;
                blockEntity.inventoryChanged();
            }
        } else {
            if (blockEntity.agingProgress > 0) {
                blockEntity.agingProgress = 0;
                blockEntity.inventoryChanged();
            }
        }
    }

    private void spawnParticles(Level level, BlockPos pos) {
        if (agingProgress <= 0 || maxProgress <= 0) return;

        double progressRatio = (double) agingProgress / maxProgress;
        RandomSource random = level.getRandom();

        // 진행도에 따른 파티클 생성 확률 및 종류 결정
        if (random.nextFloat() < 0.2F + (progressRatio * 0.3F)) {
            double x = pos.getX() + 0.3D + random.nextDouble() * 0.4D;
            double y = pos.getY() + 0.5D + random.nextDouble() * 0.5D;
            double z = pos.getZ() + 0.3D + random.nextDouble() * 0.4D;

            if (progressRatio < 0.5) {
                // 초기 단계: 약한 연기 파티클
                level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, x, y, z, 0.0D, 0.02D, 0.0D);
            } else if (progressRatio < 0.9) {
                // 중간 단계: 효과 파티클 (보글보글 느낌)
                level.addParticle(net.minecraft.core.particles.ParticleTypes.EFFECT, x, y, z, 0.0D, 0.05D, 0.0D);
            } else {
                // 거의 완료 단계: 보글보글 파티클 강화
                level.addParticle(net.minecraft.core.particles.ParticleTypes.BUBBLE_POP, x, y, z, 0.0D, 0.08D, 0.0D);
                if (random.nextFloat() < 0.5F) {
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.MYCELIUM, x, y, z, 0.0D, 0.02D, 0.0D);
                }
            }
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryOptional.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidOptional.cast();
        }

        return super.getCapability(cap, side);
    }

    // 데이터를 저장할 때 (서버 -> NBT)
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("aging_progress", this.agingProgress);
        tag.putInt("max_progress", this.maxProgress);
        tag.put("inventory", inventory.serializeNBT());
        tag.put("fluid", fluidTank.writeToNBT(new CompoundTag()));
    }

    // 데이터를 불러올 때 (NBT -> 객체)
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.agingProgress = tag.getInt("aging_progress");
        this.maxProgress = tag.getInt("max_progress");
        inventory.deserializeNBT(tag.getCompound("inventory"));
        fluidTank.readFromNBT(tag.getCompound("fluid"));
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryOptional.invalidate();
        fluidOptional.invalidate();
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }
}
