package com.potan.koreandelight.block.custom;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.blockentity.OnggiBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
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
     * Shift+우클릭 시 뚜껑을 여닫고, 그냥 우클릭 시 옹기 내부의 아이템을 꺼냅니다.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // Shift + 우클릭 시 뚜껑을 여닫습니다.
        if (player.isShiftKeyDown()) {
            level.setBlock(pos, state.setValue(HAS_LID, !state.getValue(HAS_LID)), 3);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // 뚜껑이 닫혀있으면 상호작용(아이템 꺼내기)이 불가능합니다.
        if (state.getValue(HAS_LID)) {
            return InteractionResult.PASS;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof OnggiBlockEntity onggi) {
            ItemStack outputItem = onggi.getInventory().getItem(0);

            // 옹기 안에 꺼낼 아이템이 존재할 경우
            if (!outputItem.isEmpty()) {
                // 인벤토리에서 아이템을 안전하게 꺼내어 플레이어에게 줍니다.
                ItemStack extracted = onggi.getInventory().removeItem(0, 64);
                if (!player.getInventory().add(extracted)) {
                    player.drop(extracted, false);
                }
                onggi.setChanged();
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    /**
     * 1.21.1 사양: 플레이어가 손에 아이템을 들고 옹기 블록을 우클릭했을 때 호출됩니다.
     * Shift+우클릭 시 아이템을 들고 있어도 뚜껑을 제어할 수 있도록 보완하며, 
     * 뚜껑이 열려있다면 옹기 내부에 아이템을 안전하게 집어넣습니다.
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Shift + 우클릭 시 아이템을 들고 있어도 뚜껑을 여닫을 수 있게 지원합니다.
        if (player.isShiftKeyDown()) {
            level.setBlock(pos, state.setValue(HAS_LID, !state.getValue(HAS_LID)), 3);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        // 뚜껑이 닫혀있으면 아이템 투입을 시도할 수 없습니다.
        if (state.getValue(HAS_LID)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof OnggiBlockEntity onggi)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        // [양동이 유체 상호작용 - 주입 및 퍼내기 복구]
        if (heldItem.getItem() instanceof net.minecraft.world.item.BucketItem bucketItem) {
            
            // 1. 빈 양동이인 경우 -> 옹기 속 액체(1000mB 이상)를 퍼올립니다.
            if (heldItem.is(net.minecraft.world.item.Items.BUCKET)) {
                if (onggi.getStoredFluid() != net.minecraft.world.level.material.Fluids.EMPTY && onggi.getStoredFluidAmount() >= 1000) {
                    ItemStack filledBucket = new ItemStack(onggi.getStoredFluid().getBucket());
                    
                    // 빈 양동이 1개를 유체 양동이로 성공적으로 교체해 줍니다.
                    if (heldItem.getCount() == 1) {
                        player.setItemInHand(hand, filledBucket);
                    } else {
                        heldItem.shrink(1);
                        if (!player.getInventory().add(filledBucket)) {
                            player.drop(filledBucket, false);
                        }
                    }
                    
                    // 옹기의 유체를 완전히 비워줍니다.
                    onggi.setStoredFluid(net.minecraft.world.level.material.Fluids.EMPTY, 0);
                    onggi.setChanged();
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            }
            
            // 2. 가득 찬 유체 양동이인 경우 -> 옹기 내부로 유체 1000mB를 투입합니다.
            net.minecraft.world.level.material.Fluid fluidInBucket = net.minecraft.world.level.material.Fluids.EMPTY;
            if (heldItem.is(net.minecraft.world.item.Items.WATER_BUCKET)) {
                fluidInBucket = net.minecraft.world.level.material.Fluids.WATER;
            } else if (heldItem.is(com.potan.koreandelight.item.ModItems.SOY_SAUCE_BUCKET.get())) {
                fluidInBucket = com.potan.koreandelight.fluid.ModFluids.SOURCE_SOY_SAUCE.get();
            }
            
            if (fluidInBucket != net.minecraft.world.level.material.Fluids.EMPTY) {
                // 옹기 액체 탱크가 비어있거나, 같은 종류의 유체이면서 가득 차지 않은 경우
                if (onggi.getStoredFluid() == net.minecraft.world.level.material.Fluids.EMPTY || 
                   (onggi.getStoredFluid() == fluidInBucket && onggi.getStoredFluidAmount() < 1000)) {
                    
                    // 옹기에 1000mB(1양동이 분량) 만큼 액체를 주입합니다.
                    onggi.setStoredFluid(fluidInBucket, 1000);
                    
                    // 서바이벌 모드일 경우 사용한 양동이를 빈 양동이로 돌려줍니다.
                    if (!player.isCreative()) {
                        player.setItemInHand(hand, new ItemStack(net.minecraft.world.item.Items.BUCKET));
                    }
                    onggi.setChanged();
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            }
        }

        ItemStack outputItem = onggi.getInventory().getItem(0);

        // 아이템을 옹기에 집어넣는 경우
        if (!heldItem.isEmpty()) {
            // 이미 옹기 안에 아이템이 차있다면 추가 투입이 불가하므로 스킵 처리합니다.
            if (!outputItem.isEmpty()) {
                return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            }
            
            // 들고 있는 아이템 중 1개를 옹기 인벤토리로 안전하게 이관합니다.
            ItemStack toInsert = heldItem.copy();
            toInsert.setCount(1);
            onggi.getInventory().setItem(0, toInsert);
            heldItem.shrink(1);
            onggi.setChanged();
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
