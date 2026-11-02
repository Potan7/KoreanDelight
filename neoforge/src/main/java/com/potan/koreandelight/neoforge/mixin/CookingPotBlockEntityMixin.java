package com.potan.koreandelight.neoforge.mixin;

import com.potan.koreandelight.item.ModFoodItems;
import com.potan.koreandelight.item.ModItems;
import com.potan.koreandelight.neoforge.integration.RiceBowlServing;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

@Mixin(value = CookingPotBlockEntity.class, remap = false)
public abstract class CookingPotBlockEntityMixin extends SyncedBlockEntity {
    protected CookingPotBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    public abstract ItemStack getMeal();

    @Shadow
    public abstract ItemStackHandler getInventory();

    @Unique
    private boolean koreandelight$canServeRice(ItemStack container) {
        return container.is(ModItems.RICE_BOWL.get())
                && getMeal().is(vectorwing.farmersdelight.common.registry.ModItems.COOKED_RICE.get());
    }

    @Inject(method = "isContainerValid", at = @At("HEAD"), cancellable = true)
    private void koreandelight$acceptRiceBowl(ItemStack container, CallbackInfoReturnable<Boolean> callback) {
        if (koreandelight$canServeRice(container)) {
            callback.setReturnValue(true);
        }
    }

    @Inject(method = "useStoredContainersOnMeal", at = @At("HEAD"), cancellable = true)
    private void koreandelight$fillStoredRiceBowls(CallbackInfo callback) {
        ItemStackHandler inventory = getInventory();
        ItemStack bowls = inventory.getStackInSlot(CookingPotBlockEntity.CONTAINER_SLOT);
        if (koreandelight$canServeRice(bowls)) {
            ItemStack result = RiceBowlServing.fillStoredBowls(getMeal(), bowls,
                    inventory.getStackInSlot(CookingPotBlockEntity.OUTPUT_SLOT),
                    new ItemStack(ModFoodItems.COOKED_RICE_BOWL.get()));
            if (!result.isEmpty()) {
                inventory.setStackInSlot(CookingPotBlockEntity.OUTPUT_SLOT, result);
                inventoryChanged();
            }
            callback.cancel();
        }
    }

    @Inject(method = "useHeldItemOnMeal", at = @At("HEAD"), cancellable = true)
    private void koreandelight$fillHeldRiceBowl(ItemStack container, CallbackInfoReturnable<ItemStack> callback) {
        if (koreandelight$canServeRice(container)) {
            container.shrink(1);
            getMeal().shrink(1);
            inventoryChanged();
            callback.setReturnValue(new ItemStack(ModFoodItems.COOKED_RICE_BOWL.get()));
        }
    }
}
