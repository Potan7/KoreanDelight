package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.custom.OnggiBlock;
import com.potan.koreandelight.fluid.ModFluids;
import com.potan.koreandelight.item.FluidBottleItem;
import com.potan.koreandelight.item.ModItems;
import com.potan.koreandelight.recipe.FermentationInput;
import com.potan.koreandelight.recipe.FermentationEnvironment;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.Collection;
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
    public static final int MENU_DATA_COUNT = 8;

    public enum RecipeStatus {
        EMPTY, NO_RECIPE, MISSING_INGREDIENTS, WRONG_RATIO,
        WRONG_FLUID, TOO_LITTLE_FLUID, TOO_MUCH_FLUID, UNEXPECTED_FLUID, OUTPUT_FULL, READY
    }

    public final ContainerData menuData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) agingProgress;
                case 1 -> maxProgress;
                case 2 -> BuiltInRegistries.FLUID.getId(storedFluid);
                case 3 -> storedFluidAmount;
                case 4 -> getBlockState().getValue(OnggiBlock.HAS_LID) ? 1 : 0;
                case 5 -> getRecipeStatus().ordinal();
                case 6 -> environment.ordinal();
                case 7 -> (int) (progressPerTick * 2);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return MENU_DATA_COUNT;
        }
    };

    private final SimpleContainer inventory = new SimpleContainer(TOTAL_SLOTS) {
        @Override
        public void setChanged() {
            super.setChanged();
            if (!suppressInventoryUpdates) {
                fermentation.invalidate();
                containersDirty = true;
                syncChanged();
            }
        }
    };

    // 1.21.1 사양: 멀티로더 공통 모듈 레벨에서 플랫폼 독립적으로 유체 저장을 수행하기 위한 상태 변수
    private Fluid storedFluid = Fluids.EMPTY;
    private int storedFluidAmount = 0;

    private double agingProgress = 0;
    private int maxProgress = 0;
    private FermentationEnvironment environment = FermentationEnvironment.ANY;
    private double progressPerTick = 1.0;
    private final CachedFermentation fermentation = new CachedFermentation();
    private boolean containersDirty = true;
    private boolean suppressInventoryUpdates;
    private long nextEnvironmentCheck = Long.MIN_VALUE;

    public OnggiBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OnggiBlockEntity jar) {
        if (level.isClientSide) {
            if (state.hasProperty(OnggiBlock.HAS_LID) && !state.getValue(OnggiBlock.HAS_LID)) {
                jar.spawnParticles(level, pos);
            }
            return;
        }

        handleFluidContainerSlots(jar);
        jar.refreshFermentation();
        if (jar.fermentation.recipe != null && level.getGameTime() >= jar.nextEnvironmentCheck) {
            jar.environment = jar.fermentation.recipe.getEnvironment();
            jar.progressPerTick = switch (jar.environment) {
                case ANY -> 1.0;
                case SUNNY -> jar.environment.getProgressPerTick(level.isDay() && level.canSeeSky(pos), level.isDay(), 15);
                case COOL -> jar.environment.getProgressPerTick(false, false, level.getMaxLocalRawBrightness(pos));
            };
            jar.nextEnvironmentCheck = level.getGameTime() + 20;
        }
        if (jar.fermentation.status != RecipeStatus.READY
                || state.hasProperty(OnggiBlock.HAS_LID) && !state.getValue(OnggiBlock.HAS_LID)) {
            return;
        }

        boolean starting = jar.agingProgress == 0;
        jar.agingProgress += jar.progressPerTick;
        // Progress needs saving, but does not change comparator output or the visible inventory.
        level.blockEntityChanged(pos);
        if (jar.agingProgress >= jar.maxProgress) {
            jar.suppressInventoryUpdates = true;
            try {
                jar.storedFluid = jar.fermentation.fluid.fluid();
                jar.storedFluidAmount = jar.fermentation.fluid.amount();
                for (int slot = 0; slot < INGREDIENT_SLOTS; slot++) {
                    jar.inventory.setItem(slot, jar.fermentation.contents.getItem(slot));
                }
            } finally {
                jar.suppressInventoryUpdates = false;
            }
            jar.fermentation.invalidate();
            jar.containersDirty = true;
            jar.resetProgress();
            jar.syncChanged();
        } else if (starting) {
            jar.syncChanged();
        }
    }

    private static void handleFluidContainerSlots(OnggiBlockEntity jar) {
        if (!jar.containersDirty) {
            return;
        }
        jar.containersDirty = false;
        FluidStackData result;
        jar.suppressInventoryUpdates = true;
        try {
            result = transferFluidContainer(jar.inventory, jar.storedFluid, jar.storedFluidAmount);
        } finally {
            jar.suppressInventoryUpdates = false;
        }
        if (result != null) {
            if (result.fluid() != jar.storedFluid || result.amount() != jar.storedFluidAmount) {
                jar.storedFluid = result.fluid();
                jar.storedFluidAmount = result.amount();
                jar.resetProgress();
            }
            jar.fermentation.invalidate();
            jar.containersDirty = !jar.inventory.getItem(BUCKET_IN_SLOT).isEmpty();
            jar.syncChanged();
        }
    }

    private void refreshFermentation() {
        if (level == null) {
            return;
        }
        FermentationRecipe previous = fermentation.recipe;
        if (!fermentation.update(level.getRecipeManager().getRecipes(), inventory, storedFluid, storedFluidAmount)) {
            return;
        }
        if ((previous != null && previous != fermentation.recipe) || fermentation.status != RecipeStatus.READY) {
            resetProgress();
        }
        if (fermentation.recipe != null) {
            environment = fermentation.recipe.getEnvironment();
        }
        maxProgress = fermentation.status == RecipeStatus.READY ? fermentation.duration : 0;
        nextEnvironmentCheck = Long.MIN_VALUE;
    }

    private RecipeStatus getRecipeStatus() {
        refreshFermentation();
        return fermentation.status;
    }

    // RecipeManager replaces this immutable collection on /reload; reference changes invalidate the plan.
    static final class CachedFermentation {
        private Collection<RecipeHolder<?>> recipes;
        private boolean dirty = true;
        FermentationRecipe recipe;
        RecipeStatus status = RecipeStatus.EMPTY;
        SimpleContainer contents;
        FluidStackData fluid;
        int duration;

        void invalidate() {
            dirty = true;
        }

        boolean update(Collection<RecipeHolder<?>> snapshot, SimpleContainer inventory, Fluid storedFluid, int amount) {
            if (!dirty && recipes == snapshot) {
                return false;
            }
            recipes = snapshot;
            dirty = false;
            recipe = null;
            contents = null;
            fluid = null;
            duration = 0;
            List<ItemStack> items = new ArrayList<>(INGREDIENT_SLOTS);
            for (int slot = 0; slot < INGREDIENT_SLOTS; slot++) {
                if (!inventory.getItem(slot).isEmpty()) {
                    items.add(inventory.getItem(slot));
                }
            }
            if (items.isEmpty()) {
                status = RecipeStatus.EMPTY;
                return true;
            }
            FermentationInput input = new FermentationInput(items);
            status = RecipeStatus.NO_RECIPE;
            for (RecipeHolder<?> holder : snapshot) {
                if (!(holder.value() instanceof FermentationRecipe candidate)) {
                    continue;
                }
                int batch = candidate.getBatchSize(input);
                if (batch > 0) {
                    recipe = candidate;
                    status = checkRecipeStatus(candidate, inventory, storedFluid, amount, candidate.getOutput());
                    if (status == RecipeStatus.READY) {
                        contents = createFermentedContents(candidate, inventory, candidate.getOutput());
                        fluid = createFermentedFluid(candidate, storedFluid, amount, batch);
                        duration = getBatchTime(candidate.getFermentationTime(), batch);
                    }
                    return true;
                }
                RecipeStatus reason = checkRecipeStatus(candidate, inventory, storedFluid, amount, candidate.getOutput());
                if (reason == RecipeStatus.WRONG_RATIO
                        || (reason == RecipeStatus.MISSING_INGREDIENTS && status != RecipeStatus.WRONG_RATIO)) {
                    status = reason;
                }
            }
            return true;
        }
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
        int stackLimit = result.isEmpty() ? 64 : Math.min(result.getMaxStackSize(), transformed.getMaxStackSize());
        long remaining = (long) result.getCount() * batchSize;
        if (remaining > (long) stackLimit * INGREDIENT_SLOTS) {
            return null;
        }
        for (int slot = 0; remaining > 0; slot++) {
            int count = (int) Math.min(remaining, stackLimit);
            transformed.setItem(slot, result.copyWithCount(count));
            remaining -= count;
        }
        // Containers are returned with the result, or the whole transaction waits for room.
        for (int slot = 0; slot < INGREDIENT_SLOTS; slot++) {
            ItemStack input = inventory.getItem(slot);
            if (input.isEmpty()) {
                continue;
            }
            ItemStack remainder = input.getCraftingRemainingItem();
            if (remainder.isEmpty()) {
                continue;
            }
            long count = (long) remainder.getCount() * input.getCount();
            while (count > 0) {
                int portion = (int) Math.min(count, remainder.getMaxStackSize());
                if (!transformed.addItem(remainder.copyWithCount(portion)).isEmpty()) {
                    return null;
                }
                count -= portion;
            }
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
        return (int) Math.min(Integer.MAX_VALUE, ((long) baseTime * (3L * batchSize + 1) + 3) / 4);
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
        environment = FermentationEnvironment.ANY;
        progressPerTick = 1.0;
        if (agingProgress != 0 || maxProgress != 0) {
            agingProgress = 0;
            maxProgress = 0;
            setChanged();
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
        tag.putDouble("aging_progress", this.agingProgress);
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
        // Numeric NBT conversion also reads the integer progress saved by older versions.
        this.agingProgress = tag.getDouble("aging_progress");
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
        suppressInventoryUpdates = true;
        try {
            for (int i = 0; i < TOTAL_SLOTS; i++) {
                inventory.setItem(i, list.get(i));
            }
        } finally {
            suppressInventoryUpdates = false;
        }
        fermentation.invalidate();
        containersDirty = true;
        nextEnvironmentCheck = Long.MIN_VALUE;
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
        resetProgress();
        inventory.setChanged();
    }

    public boolean insertItem(ItemStack stack) {
        if (!insertItem(inventory, stack)) {
            return false;
        }
        resetProgress();
        inventory.setChanged();
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
        fermentation.invalidate();
        containersDirty = true;
        nextEnvironmentCheck = Long.MIN_VALUE;
        resetProgress();
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
