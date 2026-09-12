package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.custom.OnggiBlock;
import com.potan.koreandelight.fluid.ModFluids;
import com.potan.koreandelight.item.ModItems;
import com.potan.koreandelight.menu.OnggiMenu;
import com.potan.koreandelight.recipe.FermentationInput;
import com.potan.koreandelight.recipe.FermentationRecipe;
import com.potan.koreandelight.recipe.FluidStackData;
import com.potan.koreandelight.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OnggiBlockEntity extends BlockEntity implements MenuProvider {
    public static final int INGREDIENT_SLOTS = 6;
    public static final int OUTPUT_SLOT = 6;
    public static final int BUCKET_IN_SLOT = 7;
    public static final int BUCKET_OUT_SLOT = 8;
    public static final int TOTAL_SLOTS = 9;

    private final SimpleContainer inventory = new SimpleContainer(TOTAL_SLOTS) {
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
    private Fluid storedFluid = Fluids.EMPTY;
    private int storedFluidAmount = 0; // 최대 1000mB (1양동이 분량)

    private int agingProgress = 0;
    private int maxProgress = 0;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> OnggiBlockEntity.this.agingProgress;
                case 1 -> OnggiBlockEntity.this.maxProgress;
                case 2 -> OnggiBlockEntity.this.storedFluidAmount;
                case 3 -> BuiltInRegistries.FLUID.getId(OnggiBlockEntity.this.storedFluid);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> OnggiBlockEntity.this.agingProgress = value;
                case 1 -> OnggiBlockEntity.this.maxProgress = value;
                case 2 -> OnggiBlockEntity.this.storedFluidAmount = value;
                case 3 -> OnggiBlockEntity.this.storedFluid = BuiltInRegistries.FLUID.byId(value);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public OnggiBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.koreandelight.onggi");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new OnggiMenu(containerId, playerInventory, this.inventory, this.dataAccess);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OnggiBlockEntity blockEntity) {
        if (level.isClientSide) {
            // 뚜껑이 열려있을 때만 내부 발효 파티클을 렌더링하여 렉을 방지합니다.
            if (state.hasProperty(OnggiBlock.HAS_LID) && !state.getValue(OnggiBlock.HAS_LID)) {
                blockEntity.spawnParticles(level, pos);
            }
            return;
        }

        // 1. GUI 버킷 슬롯 처리 (양동이 투입 및 배출)
        handleBucketSlots(blockEntity);

        // 2. 뚜껑이 열려있으면 발효 일시 정지
        if (state.hasProperty(OnggiBlock.HAS_LID) && !state.getValue(OnggiBlock.HAS_LID)) {
            return;
        }

        // 3. 재료 슬롯(0..5)에서 비어있지 않은 아이템 추출
        List<ItemStack> inputItems = new ArrayList<>();
        for (int i = 0; i < INGREDIENT_SLOTS; i++) {
            ItemStack stack = blockEntity.inventory.getItem(i);
            if (!stack.isEmpty()) {
                inputItems.add(stack);
            }
        }

        if (inputItems.isEmpty()) {
            blockEntity.resetProgress();
            return;
        }

        FermentationInput input = new FermentationInput(inputItems);
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

            // [결과물 슬롯 공간 확인]
            ItemStack recipeResult = fermentationRecipe.assemble(input, level.registryAccess());
            ItemStack currentOutput = blockEntity.inventory.getItem(OUTPUT_SLOT);
            if (!currentOutput.isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(currentOutput, recipeResult) ||
                    currentOutput.getCount() + recipeResult.getCount() > currentOutput.getMaxStackSize()) {
                    blockEntity.resetProgress();
                    return;
                }
            }

            blockEntity.maxProgress = fermentationRecipe.getFermentationTime();
            blockEntity.agingProgress++;

            if (blockEntity.agingProgress == 1 || blockEntity.agingProgress % 20 == 0) {
                blockEntity.syncChanged();
            }

            // 발효(숙성) 완료
            if (blockEntity.agingProgress >= fermentationRecipe.getFermentationTime()) {
                // 1. 입력 유체 소모 처리
                if (fermentationRecipe.getFluid().isPresent()) {
                    blockEntity.storedFluidAmount -= fermentationRecipe.getFluid().get().amount();
                    if (blockEntity.storedFluidAmount <= 0) {
                        blockEntity.storedFluid = Fluids.EMPTY;
                        blockEntity.storedFluidAmount = 0;
                    }
                }

                // 2. 결과 유체 충전 처리
                if (fermentationRecipe.getResultFluid().isPresent()) {
                    blockEntity.storedFluid = fermentationRecipe.getResultFluid().get().fluid();
                    blockEntity.storedFluidAmount += fermentationRecipe.getResultFluid().get().amount();
                }

                // 3. 재료 슬롯에서 재료 1개씩 소모
                consumeIngredients(fermentationRecipe, blockEntity.inventory);

                // 4. 결과물 슬롯에 아이템 추가
                if (currentOutput.isEmpty()) {
                    blockEntity.inventory.setItem(OUTPUT_SLOT, recipeResult.copy());
                } else {
                    currentOutput.grow(recipeResult.getCount());
                }

                blockEntity.agingProgress = 0;
                blockEntity.maxProgress = 0;
                blockEntity.syncChanged();
            }
        } else {
            blockEntity.resetProgress();
        }
    }

    private static void handleBucketSlots(OnggiBlockEntity blockEntity) {
        ItemStack bucketIn = blockEntity.inventory.getItem(BUCKET_IN_SLOT);
        ItemStack bucketOut = blockEntity.inventory.getItem(BUCKET_OUT_SLOT);

        if (bucketIn.isEmpty()) return;

        // 1. 찬 양동이로 옹기에 유체 붓기
        Fluid fluidInBucket = Fluids.EMPTY;
        if (bucketIn.is(Items.WATER_BUCKET)) {
            fluidInBucket = Fluids.WATER;
        } else if (bucketIn.is(ModItems.SOY_SAUCE_BUCKET.get())) {
            fluidInBucket = ModFluids.SOURCE_SOY_SAUCE.get();
        }

        if (fluidInBucket != Fluids.EMPTY) {
            if ((blockEntity.storedFluid == Fluids.EMPTY || blockEntity.storedFluid == fluidInBucket) && blockEntity.storedFluidAmount <= 0) {
                if (bucketOut.isEmpty() || (bucketOut.is(Items.BUCKET) && bucketOut.getCount() < bucketOut.getMaxStackSize())) {
                    blockEntity.setStoredFluid(fluidInBucket, 1000);
                    bucketIn.shrink(1);
                    if (bucketOut.isEmpty()) {
                        blockEntity.inventory.setItem(BUCKET_OUT_SLOT, new ItemStack(Items.BUCKET));
                    } else {
                        bucketOut.grow(1);
                    }
                    blockEntity.setChanged();
                }
            }
        } else if (bucketIn.is(Items.BUCKET)) {
            // 2. 빈 양동이로 옹기 속 유체 퍼내기
            if (blockEntity.storedFluid != Fluids.EMPTY && blockEntity.storedFluidAmount >= 1000) {
                Item filledBucketItem = blockEntity.storedFluid.getBucket();
                if (filledBucketItem != Items.AIR) {
                    ItemStack filledStack = new ItemStack(filledBucketItem);
                    if (bucketOut.isEmpty() || (ItemStack.isSameItemSameComponents(bucketOut, filledStack) && bucketOut.getCount() < bucketOut.getMaxStackSize())) {
                        blockEntity.setStoredFluid(Fluids.EMPTY, 0);
                        bucketIn.shrink(1);
                        if (bucketOut.isEmpty()) {
                            blockEntity.inventory.setItem(BUCKET_OUT_SLOT, filledStack);
                        } else {
                            bucketOut.grow(1);
                        }
                        blockEntity.setChanged();
                    }
                }
            }
        }
    }

    private static void consumeIngredients(FermentationRecipe recipe, SimpleContainer inventory) {
        for (Ingredient ing : recipe.getIngredients()) {
            for (int i = 0; i < INGREDIENT_SLOTS; i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty() && ing.test(stack)) {
                    stack.shrink(1);
                    break;
                }
            }
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
        if (this.storedFluid != Fluids.EMPTY) {
            tag.putString("stored_fluid", BuiltInRegistries.FLUID.getKey(this.storedFluid).toString());
        }
        tag.putInt("stored_fluid_amount", this.storedFluidAmount);

        NonNullList<ItemStack> list = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            list.set(i, inventory.getItem(i));
        }
        ContainerHelper.saveAllItems(tag, list, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.agingProgress = tag.getInt("aging_progress");
        this.maxProgress = tag.getInt("max_progress");

        // 1.21.1 사양: 유체 NBT 상태 로드
        if (tag.contains("stored_fluid")) {
            this.storedFluid = BuiltInRegistries.FLUID.get(
                net.minecraft.resources.ResourceLocation.parse(tag.getString("stored_fluid"))
            );
        } else {
            this.storedFluid = Fluids.EMPTY;
        }
        this.storedFluidAmount = tag.getInt("stored_fluid_amount");

        NonNullList<ItemStack> list = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, list, registries);
        for (int i = 0; i < TOTAL_SLOTS; i++) {
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

    public Fluid getStoredFluid() {
        return storedFluid;
    }

    public int getStoredFluidAmount() {
        return storedFluidAmount;
    }

    public void setStoredFluid(Fluid fluid, int amount) {
        this.storedFluid = fluid;
        this.storedFluidAmount = amount;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}

