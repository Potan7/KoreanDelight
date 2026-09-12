package com.potan.koreandelight.block.custom;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class OnggiBlock extends Block implements EntityBlock {
    public static final BooleanProperty HAS_LID = BooleanProperty.create("has_lid");
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);

    public OnggiBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_LID, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_LID);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get().create(pos, state);
    }

    /**
     * 1.21.1 사양: 플레이어가 빈손으로 옹기 블록을 우클릭했을 때 호출됩니다.
     * Shift+우클릭 시 뚜껑을 여닫고, 뚜껑이 열려 있을 때 우클릭하면 내부 GUI를 엽니다.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // Shift + 우클릭 시 뚜껑을 여닫습니다.
        if (player.isShiftKeyDown()) {
            boolean newLidState = !state.getValue(HAS_LID);
            level.setBlock(pos, state.setValue(HAS_LID, newLidState), 3);
            level.playSound(null, pos, newLidState ? net.minecraft.sounds.SoundEvents.BARREL_CLOSE : net.minecraft.sounds.SoundEvents.BARREL_OPEN, net.minecraft.sounds.SoundSource.BLOCKS, 0.7F, 0.9F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // 뚜껑이 닫혀있으면 GUI가 열리지 않고 닫혀있음을 알립니다.
        if (state.getValue(HAS_LID)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("block.koreandelight.onggi.lid_closed"), true);
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BARREL_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, 1.2F);
            return InteractionResult.CONSUME;
        }

        // 뚜껑이 열려있으면 GUI 화면을 엽니다.
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof OnggiBlockEntity onggi) {
            player.openMenu(onggi);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /**
     * 1.21.1 사양: 플레이어가 손에 아이템을 들고 옹기 블록을 우클릭했을 때 호출됩니다.
     * Shift+우클릭 시 뚜껑을 제어하며,
     * 뚜껑이 열려있을 때 양동이로는 즉시 유체를 넣고 뺄 수 있고, 일반 아이템일 경우 GUI를 엽니다.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Shift + 우클릭 시 아이템을 들고 있어도 뚜껑을 여닫을 수 있게 지원합니다.
        if (player.isShiftKeyDown()) {
            boolean newLidState = !state.getValue(HAS_LID);
            level.setBlock(pos, state.setValue(HAS_LID, newLidState), 3);
            level.playSound(null, pos, newLidState ? net.minecraft.sounds.SoundEvents.BARREL_CLOSE : net.minecraft.sounds.SoundEvents.BARREL_OPEN, net.minecraft.sounds.SoundSource.BLOCKS, 0.7F, 0.9F);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        // 뚜껑이 닫혀있으면 월드 상호작용을 처리하지 않고 기본 블록 상호작용으로 넘깁니다.
        if (state.getValue(HAS_LID)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof OnggiBlockEntity onggi)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        // [양동이 유체 상호작용 - 월드에서 직접 주입 및 퍼내기 지원]
        if (heldItem.getItem() instanceof net.minecraft.world.item.BucketItem) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }

            // 1. 빈 양동이인 경우 -> 옹기 속 액체(1000mB 이상)를 퍼올립니다.
            if (heldItem.is(net.minecraft.world.item.Items.BUCKET)) {
                if (onggi.getStoredFluid() != net.minecraft.world.level.material.Fluids.EMPTY && onggi.getStoredFluidAmount() >= 1000) {
                    ItemStack filledBucket = new ItemStack(onggi.getStoredFluid().getBucket());

                    if (heldItem.getCount() == 1) {
                        player.setItemInHand(hand, filledBucket);
                    } else {
                        heldItem.shrink(1);
                        if (!player.getInventory().add(filledBucket)) {
                            player.drop(filledBucket, false);
                        }
                    }

                    onggi.setStoredFluid(net.minecraft.world.level.material.Fluids.EMPTY, 0);
                    onggi.setChanged();
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            // 2. 가득 찬 유체 양동이인 경우 -> 옹기 내부로 유체 1000mB를 투입합니다.
            net.minecraft.world.level.material.Fluid fluidInBucket = net.minecraft.world.level.material.Fluids.EMPTY;
            if (heldItem.is(net.minecraft.world.item.Items.WATER_BUCKET)) {
                fluidInBucket = net.minecraft.world.level.material.Fluids.WATER;
            } else if (heldItem.is(com.potan.koreandelight.item.ModItems.SOY_SAUCE_BUCKET.get())) {
                fluidInBucket = com.potan.koreandelight.fluid.ModFluids.SOURCE_SOY_SAUCE.get();
            }

            if (fluidInBucket != net.minecraft.world.level.material.Fluids.EMPTY) {
                if (onggi.getStoredFluid() == net.minecraft.world.level.material.Fluids.EMPTY || 
                   (onggi.getStoredFluid() == fluidInBucket && onggi.getStoredFluidAmount() < 1000)) {

                    onggi.setStoredFluid(fluidInBucket, 1000);

                    if (!player.isCreative()) {
                        player.setItemInHand(hand, new ItemStack(net.minecraft.world.item.Items.BUCKET));
                    }
                    onggi.setChanged();
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }

        // 양동이가 아닌 일반 아이템을 들고 우클릭한 경우 GUI를 엽니다.
        if (!level.isClientSide) {
            player.openMenu(onggi);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntityTypes.ONGGI_BLOCK_ENTITY.get(), OnggiBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof OnggiBlockEntity onggi) {
                Containers.dropContents(level, pos, onggi.getInventory());
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> type1, BlockEntityType<E> type2, BlockEntityTicker<E> ticker) {
        return type2 == type1 ? (BlockEntityTicker<A>) ticker : null;
    }
}
