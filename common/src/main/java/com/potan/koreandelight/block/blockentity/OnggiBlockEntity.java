package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.custom.OnggiBlock;
import com.potan.koreandelight.recipe.FermentationInput;
import com.potan.koreandelight.recipe.FermentationRecipe;
import com.potan.koreandelight.recipe.FluidStackData;
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

    // 1.21.1 사양: 멀티로더 공통 모듈 레벨에서 플랫폼 독립적으로 유체 저장을 수행하기 위한 상태 변수
    private net.minecraft.world.level.material.Fluid storedFluid = net.minecraft.world.level.material.Fluids.EMPTY;
    private int storedFluidAmount = 0; // 최대 1000mB (1양동이 분량)

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
            blockEntity.resetProgress();
            return;
        }

        if (state.hasProperty(OnggiBlock.HAS_LID) && !state.getValue(OnggiBlock.HAS_LID)) {
            blockEntity.resetProgress();
            return;
        }

        FermentationInput input = new FermentationInput(inputStack);
        Optional<FermentationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.FERMENTATION_RECIPE_TYPE.get(), input, level)
                .map(holder -> holder.value());

        if (recipe.isPresent()) {
            FermentationRecipe fermentationRecipe = recipe.get();

            // [유체 조건 검증 - 입력 유체 확인]
            if (fermentationRecipe.getFluid().isPresent()) {
                FluidStackData inputFluid = fermentationRecipe.getFluid().get();
                if (blockEntity.storedFluidAmount < inputFluid.amount() ||
                    !blockEntity.storedFluid.isSame(inputFluid.fluid())) {
                    blockEntity.resetProgress();
                    return;
                }
            }

            // [유체 조건 검증 - 결과물 수용 가능 여부 확인]
            if (fermentationRecipe.getResultFluid().isPresent()) {
                FluidStackData outputFluid = fermentationRecipe.getResultFluid().get();
                int currentAmount = blockEntity.storedFluidAmount;
                int inputAmount = fermentationRecipe.getFluid().isPresent() ? fermentationRecipe.getFluid().get().amount() : 0;
                int outputAmount = outputFluid.amount();
                int remainingAmount = currentAmount - inputAmount;

                // 옹기의 유체 최대 용량은 1000mB입니다.
                if (remainingAmount + outputAmount > 1000) {
                    blockEntity.resetProgress();
                    return;
                }

                // 기존 유체와 결과물 유체 종류가 일치하는지 확인
                if (remainingAmount > 0 && !blockEntity.storedFluid.isSame(outputFluid.fluid())) {
                    blockEntity.resetProgress();
                    return;
                }
            }

            blockEntity.maxProgress = fermentationRecipe.getFermentation_time();
            blockEntity.agingProgress++;
            
            if (blockEntity.agingProgress == 1 || blockEntity.agingProgress % 20 == 0) {
                blockEntity.syncChanged();
            }

            // 발효(숙성) 완료
            if (blockEntity.agingProgress >= fermentationRecipe.getFermentation_time()) {
                ItemStack result = fermentationRecipe.assemble(input, level.registryAccess()).copy();
                result.setCount(inputStack.getCount());

                // 1. 입력 유체 소모 처리
                if (fermentationRecipe.getFluid().isPresent()) {
                    blockEntity.storedFluidAmount -= fermentationRecipe.getFluid().get().amount();
                    if (blockEntity.storedFluidAmount <= 0) {
                        blockEntity.storedFluid = net.minecraft.world.level.material.Fluids.EMPTY;
                        blockEntity.storedFluidAmount = 0;
                    }
                }

                // 2. 결과 유체 충전 처리
                if (fermentationRecipe.getResultFluid().isPresent()) {
                    blockEntity.storedFluid = fermentationRecipe.getResultFluid().get().fluid();
                    blockEntity.storedFluidAmount += fermentationRecipe.getResultFluid().get().amount();
                }

                blockEntity.inventory.setItem(0, result);
                blockEntity.agingProgress = 0;
                blockEntity.maxProgress = 0;
                blockEntity.syncChanged();
            }
        } else {
            blockEntity.resetProgress();
        }
    }

    private void spawnParticles(Level level, BlockPos pos) {
        if (agingProgress <= 0 || maxProgress <= 0) return;
        double progressRatio = (double) agingProgress / maxProgress;
        RandomSource random = level.getRandom();

        if (random.nextFloat() < 0.2F + (progressRatio * 0.3F)) {
            double x = pos.getX() + 0.3D + random.nextDouble() * 0.4D;
            double y = pos.getY() + 0.95D + random.nextDouble() * 0.25D;
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

    private void resetProgress() {
        if (agingProgress != 0 || maxProgress != 0) {
            agingProgress = 0;
            maxProgress = 0;
            syncChanged();
        }
    }

    private void syncChanged() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("aging_progress", this.agingProgress);
        tag.putInt("max_progress", this.maxProgress);
        
        // 1.21.1 사양: 유체 NBT 상태 저장
        if (this.storedFluid != net.minecraft.world.level.material.Fluids.EMPTY) {
            tag.putString("stored_fluid", net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(this.storedFluid).toString());
        }
        tag.putInt("stored_fluid_amount", this.storedFluidAmount);

        NonNullList<ItemStack> list = NonNullList.withSize(1, ItemStack.EMPTY);
        list.set(0, inventory.getItem(0));
        ContainerHelper.saveAllItems(tag, list, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.agingProgress = tag.getInt("aging_progress");
        this.maxProgress = tag.getInt("max_progress");
        
        // 1.21.1 사양: 유체 NBT 상태 로드
        if (tag.contains("stored_fluid")) {
            this.storedFluid = net.minecraft.core.registries.BuiltInRegistries.FLUID.get(
                net.minecraft.resources.ResourceLocation.parse(tag.getString("stored_fluid"))
            );
        } else {
            this.storedFluid = net.minecraft.world.level.material.Fluids.EMPTY;
        }
        this.storedFluidAmount = tag.getInt("stored_fluid_amount");

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

    public net.minecraft.world.level.material.Fluid getStoredFluid() {
        return storedFluid;
    }

    public int getStoredFluidAmount() {
        return storedFluidAmount;
    }

    public void setStoredFluid(net.minecraft.world.level.material.Fluid fluid, int amount) {
        this.storedFluid = fluid;
        this.storedFluidAmount = amount;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
