package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.custom.OnggiBlock;
import com.potan.koreandelight.fluid.ModFluids;
import com.potan.koreandelight.item.FluidBottleItem;
import com.potan.koreandelight.item.ModItems;
import com.potan.koreandelight.recipe.FermentationInput;
import com.potan.koreandelight.recipe.FermentationRecipe;
import com.potan.koreandelight.recipe.FluidStackData;
import com.potan.koreandelight.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OnggiBlockEntity extends BlockEntity {
    public static final int INGREDIENT_SLOTS = 4;
    public static final int BUCKET_IN_SLOT = 4;
    public static final int BUCKET_OUT_SLOT = 5;
    public static final int TOTAL_SLOTS = 6;
    public static final int MAX_INGREDIENT_STACK = 8;
    public static final int BUCKET_VOLUME = 1000;
    public static final int BOTTLE_VOLUME = 250;
    public static final int TANK_CAPACITY = 8 * BUCKET_VOLUME;

    public enum RecipeStatus {
        EMPTY, NO_RECIPE, MISSING_INGREDIENTS, WRONG_RATIO,
        WRONG_FLUID, TOO_LITTLE_FLUID, TOO_MUCH_FLUID, UNEXPECTED_FLUID, OUTPUT_FULL, READY
    }

    public final ContainerData menuData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> agingProgress;
                case 1 -> maxProgress;
                case 2 -> BuiltInRegistries.FLUID.getId(storedFluid);
                case 3 -> storedFluidAmount;
                case 4 -> getBlockState().getValue(OnggiBlock.HAS_LID) ? 1 : 0;
                case 5 -> getRecipeStatus().ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

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
    private int storedFluidAmount = 0;

    private int agingProgress = 0;
    private int maxProgress = 0;

    public OnggiBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OnggiBlockEntity blockEntity) {
        if (level.isClientSide) {
            // 뚜껑이 열려있을 때만 내부 발효 파티클을 렌더링하여 렉을 방지합니다.
            if (state.hasProperty(OnggiBlock.HAS_LID) && !state.getValue(OnggiBlock.HAS_LID)) {
                blockEntity.spawnParticles(level, pos);
            }
            return;
        }

        // 1. 양동이 슬롯 처리
        handleFluidContainerSlots(blockEntity);

        // 2. 뚜껑이 열려있으면 발효 일시 정지
        if (state.hasProperty(OnggiBlock.HAS_LID) && !state.getValue(OnggiBlock.HAS_LID)) {
            return;
        }

        // 3. 재료 슬롯(0..3)에서 비어있지 않은 아이템 추출
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

            int batchSize = fermentationRecipe.getBatchSize(input);
            FluidStackData transformedFluid = createFermentedFluid(fermentationRecipe,
                    blockEntity.storedFluid, blockEntity.storedFluidAmount, batchSize);
            if (transformedFluid == null) {
                blockEntity.resetProgress();
                return;
            }

            // Check the replacement contents before consuming any items or fluid.
            ItemStack recipeResult = fermentationRecipe.assemble(input, level.registryAccess());
            SimpleContainer transformed = createFermentedContents(fermentationRecipe, blockEntity.inventory, recipeResult);
            if (transformed == null) {
                blockEntity.resetProgress();
                return;
            }

            blockEntity.maxProgress = getBatchTime(fermentationRecipe.getFermentationTime(), batchSize);
            blockEntity.agingProgress++;

            if (blockEntity.agingProgress == 1 || blockEntity.agingProgress % 20 == 0) {
                blockEntity.syncChanged();
            }

            // 발효(숙성) 완료
            if (blockEntity.agingProgress >= blockEntity.maxProgress) {
                blockEntity.storedFluid = transformedFluid.fluid();
                blockEntity.storedFluidAmount = transformedFluid.amount();

                // 3. Replace the contents in the same four ingredient slots.
                for (int i = 0; i < INGREDIENT_SLOTS; i++) {
                    blockEntity.inventory.setItem(i, transformed.getItem(i));
                }

                blockEntity.agingProgress = 0;
                blockEntity.maxProgress = 0;
                blockEntity.syncChanged();
            }
        } else {
            blockEntity.resetProgress();
        }
    }

    private static void handleFluidContainerSlots(OnggiBlockEntity blockEntity) {
        FluidStackData result = transferFluidContainer(blockEntity.inventory, blockEntity.storedFluid, blockEntity.storedFluidAmount);
        if (result != null) {
            if (result.fluid() != blockEntity.storedFluid || result.amount() != blockEntity.storedFluidAmount) {
                blockEntity.setStoredFluid(result.fluid(), result.amount());
            }
            blockEntity.inventory.setChanged();
        }
    }

    private RecipeStatus getRecipeStatus() {
        if (level == null) {
            return RecipeStatus.EMPTY;
        }
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < INGREDIENT_SLOTS; i++) {
            if (!inventory.getItem(i).isEmpty()) {
                items.add(inventory.getItem(i));
            }
        }
        if (items.isEmpty()) {
            return RecipeStatus.EMPTY;
        }
        FermentationInput input = new FermentationInput(items);
        var recipe = level.getRecipeManager().getRecipeFor(ModRecipes.FERMENTATION_RECIPE_TYPE.get(), input, level);
        if (recipe.isPresent()) {
            FermentationRecipe matched = recipe.get().value();
            return checkRecipeStatus(matched, inventory, storedFluid, storedFluidAmount,
                    matched.assemble(input, level.registryAccess()));
        }
        RecipeStatus reason = RecipeStatus.NO_RECIPE;
        for (var holder : level.getRecipeManager().getAllRecipesFor(ModRecipes.FERMENTATION_RECIPE_TYPE.get())) {
            RecipeStatus candidate = checkRecipeStatus(holder.value(), inventory, storedFluid, storedFluidAmount,
                    holder.value().getResultItem(level.registryAccess()));
            if (candidate == RecipeStatus.WRONG_RATIO) {
                return candidate;
            }
            if (candidate == RecipeStatus.MISSING_INGREDIENTS) {
                reason = candidate;
            }
        }
        return reason;
    }

    static RecipeStatus checkRecipeStatus(FermentationRecipe recipe, SimpleContainer inventory,
                                         Fluid fluid, int amount, ItemStack result) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < INGREDIENT_SLOTS; i++) {
            if (!inventory.getItem(i).isEmpty()) {
                items.add(inventory.getItem(i));
            }
        }
        if (items.isEmpty()) {
            return RecipeStatus.EMPTY;
        }
        int batchSize = recipe.getBatchSize(new FermentationInput(items));
        if (batchSize == 0) {
            var ingredients = recipe.getIngredients().stream().filter(ingredient -> !ingredient.isEmpty()).toList();
            if (items.stream().anyMatch(stack -> ingredients.stream().noneMatch(ingredient -> ingredient.test(stack)))) {
                return RecipeStatus.NO_RECIPE;
            }
            return ingredients.stream().anyMatch(ingredient -> items.stream().noneMatch(ingredient::test))
                    ? RecipeStatus.MISSING_INGREDIENTS : RecipeStatus.WRONG_RATIO;
        }
        if (recipe.getFluid().isPresent()) {
            FluidStackData required = recipe.getFluid().get();
            long requiredAmount = (long) required.amount() * batchSize;
            if (amount == 0) {
                return RecipeStatus.TOO_LITTLE_FLUID;
            }
            if (!fluid.isSame(required.fluid())) {
                return RecipeStatus.WRONG_FLUID;
            }
            if (amount != requiredAmount) {
                return amount < requiredAmount ? RecipeStatus.TOO_LITTLE_FLUID : RecipeStatus.TOO_MUCH_FLUID;
            }
        } else if (amount != 0) {
            return RecipeStatus.UNEXPECTED_FLUID;
        }
        return createFermentedFluid(recipe, fluid, amount, batchSize) == null
                || createFermentedContents(recipe, inventory, result) == null
                ? RecipeStatus.OUTPUT_FULL : RecipeStatus.READY;
    }

    public static boolean isBucketItem(ItemStack stack) {
        return stack.getItem() instanceof BucketItem || stack.getItem() instanceof MilkBucketItem
                || stack.is(Items.BUCKET) || stack.is(Items.POWDER_SNOW_BUCKET);
    }

    public static boolean isFluidContainer(ItemStack stack) {
        return isBucketItem(stack) || stack.is(Items.GLASS_BOTTLE)
                || stack.getItem() instanceof FluidBottleItem || isWaterBottle(stack);
    }

    private static boolean isWaterBottle(ItemStack stack) {
        return stack.is(Items.POTION) && stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER);
    }

    private static ItemStack filledBottle(Fluid fluid) {
        if (fluid.isSame(Fluids.WATER)) {
            return PotionContents.createItemStack(Items.POTION, Potions.WATER);
        }
        if (BuiltInRegistries.FLUID.getKey(fluid).getNamespace().equals("koreandelight")) {
            if (fluid.isSame(ModFluids.SOURCE_SOY_SAUCE.get())) {
                return new ItemStack(ModItems.SOY_SAUCE_BOTTLE.get());
            }
            if (fluid.isSame(ModFluids.SOURCE_FISH_SAUCE.get())) {
                return new ItemStack(ModItems.FISH_SAUCE.get());
            }
        }
        return ItemStack.EMPTY;
    }

    // Return null when output cannot accept the container; do not change the tank or input.
    public static FluidStackData transferFluidContainer(SimpleContainer inventory, Fluid fluid, int amount) {
        ItemStack input = inventory.getItem(BUCKET_IN_SLOT);
        if (input.isEmpty() || !isFluidContainer(input)) {
            return null;
        }
        ItemStack result = input.copyWithCount(1);
        Fluid nextFluid = fluid;
        int nextAmount = amount;
        if (input.is(Items.GLASS_BOTTLE)) {
            ItemStack bottle = amount >= BOTTLE_VOLUME ? filledBottle(fluid) : ItemStack.EMPTY;
            if (!bottle.isEmpty()) {
                result = bottle;
                nextAmount = amount - BOTTLE_VOLUME;
                nextFluid = nextAmount == 0 ? Fluids.EMPTY : fluid;
            }
        } else if (input.getItem() instanceof FluidBottleItem || isWaterBottle(input)) {
            Fluid bottleFluid = input.getItem() instanceof FluidBottleItem bottle ? bottle.getFluid() : Fluids.WATER;
            if (amount + BOTTLE_VOLUME <= TANK_CAPACITY && (amount == 0 || fluid.isSame(bottleFluid))) {
                result = new ItemStack(Items.GLASS_BOTTLE);
                nextFluid = bottleFluid;
                nextAmount = amount + BOTTLE_VOLUME;
            }
        } else if (input.is(Items.BUCKET)) {
            if (amount >= BUCKET_VOLUME && fluid != Fluids.EMPTY && fluid.getBucket() != Items.AIR) {
                result = new ItemStack(fluid.getBucket());
                nextAmount = amount - BUCKET_VOLUME;
                nextFluid = nextAmount == 0 ? Fluids.EMPTY : fluid;
            }
        } else if (input.getItem() instanceof BucketItem bucket
                && !(bucket instanceof MobBucketItem) && !input.is(Items.POWDER_SNOW_BUCKET)
                && bucket.content != Fluids.EMPTY && amount + BUCKET_VOLUME <= TANK_CAPACITY
                && (amount == 0 || fluid.isSame(bucket.content))) {
            result = new ItemStack(Items.BUCKET);
            nextFluid = bucket.content;
            nextAmount = amount + BUCKET_VOLUME;
        }
        ItemStack output = inventory.getItem(BUCKET_OUT_SLOT);
        if (!output.isEmpty() && (!ItemStack.isSameItemSameComponents(output, result)
                || output.getCount() + result.getCount() > output.getMaxStackSize())) {
            return null;
        }
        input.shrink(1);
        if (output.isEmpty()) {
            inventory.setItem(BUCKET_OUT_SLOT, result);
        } else {
            output.grow(1);
        }
        return new FluidStackData(nextFluid, nextAmount);
    }

    static SimpleContainer createFermentedContents(FermentationRecipe recipe, SimpleContainer inventory, ItemStack result) {
        List<ItemStack> contents = new ArrayList<>();
        for (int i = 0; i < INGREDIENT_SLOTS; i++) {
            contents.add(inventory.getItem(i));
        }
        int batchSize = recipe.getBatchSize(new FermentationInput(contents));
        if (batchSize < 1) {
            return null;
        }
        SimpleContainer transformed = new SimpleContainer(INGREDIENT_SLOTS);
        if (result.isEmpty()) {
            return transformed;
        }
        int stackLimit = Math.min(result.getMaxStackSize(), transformed.getMaxStackSize());
        long remaining = (long) result.getCount() * batchSize;
        if (remaining > (long) stackLimit * INGREDIENT_SLOTS) {
            return null;
        }
        for (int slot = 0; remaining > 0; slot++) {
            int count = (int) Math.min(remaining, stackLimit);
            transformed.setItem(slot, result.copyWithCount(count));
            remaining -= count;
        }
        return transformed;
    }

    static FluidStackData createFermentedFluid(FermentationRecipe recipe, Fluid fluid, int amount, int batchSize) {
        if (batchSize < 1) {
            return null;
        }
        if (recipe.getFluid().isPresent()) {
            FluidStackData required = recipe.getFluid().get();
            if (!fluid.isSame(required.fluid()) || amount != (long) required.amount() * batchSize) {
                return null;
            }
        } else if (amount != 0) {
            return null;
        }
        if (recipe.getResultFluid().isEmpty()) {
            return new FluidStackData(Fluids.EMPTY, 0);
        }
        FluidStackData result = recipe.getResultFluid().get();
        long outputAmount = (long) result.amount() * batchSize;
        return outputAmount <= TANK_CAPACITY ? new FluidStackData(result.fluid(), (int) outputAmount) : null;
    }

    static int getBatchTime(int baseTime, int batchSize) {
        return Math.toIntExact(((long) baseTime * (3 * batchSize + 1) + 3) / 4);
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

    public void ingredientsChanged() {
        inventory.setChanged();
        resetProgress();
    }

    public boolean insertItem(ItemStack stack) {
        if (!insertItem(inventory, stack)) {
            return false;
        }
        inventory.setChanged();
        resetProgress();
        return true;
    }

    static boolean insertItem(SimpleContainer inventory, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        // Merge first, so repeated clicks do not occupy every ingredient slot.
        for (int i = 0; i < INGREDIENT_SLOTS; i++) {
            ItemStack stored = inventory.getItem(i);
            if (ItemStack.isSameItemSameComponents(stored, stack)
                    && stored.getCount() < Math.min(stored.getMaxStackSize(), MAX_INGREDIENT_STACK)) {
                stored.grow(1);
                return true;
            }
        }
        for (int i = 0; i < INGREDIENT_SLOTS; i++) {
            if (inventory.getItem(i).isEmpty()) {
                inventory.setItem(i, stack.copyWithCount(1));
                return true;
            }
        }
        return false;
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
        resetProgress();
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
