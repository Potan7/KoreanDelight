package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.recipe.KimjangRecipe;
import com.potan.koreandelight.recipe.ModRecipes;
import com.potan.koreandelight.tag.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;

import javax.annotation.Nullable;

public class KimjangBasinEntity extends SyncedBlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();   // 저장 필요함을 마인크래프트에 알림
            inventoryChanged(); // SyncedBlockEntity의 클라이언트 동기화 메서드
        }
    };

    private final LazyOptional<IItemHandler> inventoryCapability = LazyOptional.of(() -> inventory);

    public KimjangBasinEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.KIMJANG_BASIN_ENTITY.get(), pos, state);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        inventory.deserializeNBT(compound.getCompound("Inventory"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Inventory", inventory.serializeNBT());
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    /**
     * 재료를 대야에 추가합니다.
     * @return 성공 여부
     */
    public boolean addIngredient(Player player, ItemStack stackInHand, Level level) {
        // 등록된 김장 레시피들 확인
        for (KimjangRecipe recipe : level.getRecipeManager().getAllRecipesFor(ModRecipes.KIMJANG_RECIPE_TYPE.get())) {
            // 채소 확인
            if (recipe.matchesVegetable(stackInHand)) {
                if (inventory.getStackInSlot(0).isEmpty()) {
                    inventory.insertItem(0, stackInHand.split(1), false);
                    return true;
                } else {
                    player.displayClientMessage(Component.translatable("msg.koreandelight.already_has_vegetable"), true);
                    return false;
                }
            }

            // 양념 확인
            if (recipe.matchesSeasoning(stackInHand)) {
                for (int i = 1; i < inventory.getSlots(); i++) {
                    if (inventory.getStackInSlot(i).isEmpty()) {
                        inventory.insertItem(i, stackInHand.split(1), false);
                        return true;
                    }
                }
                return false;
            }
        }

        // 레시피가 없으면 태그로 대체 확인
        if (stackInHand.is(ModTags.KIMJANG_VEGETABLES)) {
            if (inventory.getStackInSlot(0).isEmpty()) {
                inventory.insertItem(0, stackInHand.split(1), false);
                return true;
            } else {
                player.displayClientMessage(Component.translatable("msg.koreandelight.already_has_vegetable"), true);
                return false;
            }
        }

        if (stackInHand.is(ModTags.KIMJANG_SEASONINGS)) {
            for (int i = 1; i < inventory.getSlots(); i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    inventory.insertItem(i, stackInHand.split(1), false);
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    /**
     * 김장 작업을 수행합니다.
     * @return 성공 여부
     */
    public boolean performKimjang(Player player, Level level) {
        ItemStack vegetable = inventory.getStackInSlot(0);

        if (vegetable.isEmpty()) {
            player.displayClientMessage(Component.translatable("msg.koreandelight.need_vegetable"), true);
            return false;
        }

        // 등록된 김장 레시피 확인
        for (KimjangRecipe recipe : level.getRecipeManager().getAllRecipesFor(ModRecipes.KIMJANG_RECIPE_TYPE.get())) {
            if (recipe.matchesVegetable(vegetable)) {
                // 필요한 양념 개수 확인 및 수집
                int requiredSeasoningCount = recipe.getRequiredSeasoningCount();
                int collectedSeasonings = 0;
                int[] seasoningSlots = new int[inventory.getSlots()];

                for (int i = 1; i < inventory.getSlots(); i++) {
                    ItemStack seasoningStack = inventory.getStackInSlot(i);
                    if (!seasoningStack.isEmpty() && recipe.matchesSeasoning(seasoningStack)) {
                        seasoningSlots[collectedSeasonings] = i;
                        collectedSeasonings++;
                    }
                }

                if (collectedSeasonings < requiredSeasoningCount) {
                    player.displayClientMessage(
                        Component.literal("§c양념이 부족합니다! " + collectedSeasonings + "/" + requiredSeasoningCount),
                        true
                    );
                    return false;
                }

                // 양념 추출
                for (int i = 0; i < requiredSeasoningCount; i++) {
                    inventory.extractItem(seasoningSlots[i], 1, false);
                }

                // 채소 추출
                inventory.extractItem(0, 1, false);

                // 결과물 생성
                ItemStack result = recipe.getResultItem(player.registryAccess()).copy();
                ItemHandlerHelper.giveItemToPlayer(player, result);

                return true;
            }
        }

        // 레시피 없음
        player.displayClientMessage(Component.literal("§c이 채소에 맞는 레시피가 없습니다!"), true);
        return false;
    }
}
