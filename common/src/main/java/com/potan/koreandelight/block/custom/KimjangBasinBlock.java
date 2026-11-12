package com.potan.koreandelight.block.custom;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.blockentity.KimjangBasinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class KimjangBasinBlock extends Block implements EntityBlock {
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    private static final Part[] PARTS = Part.values();

    public KimjangBasinBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART, Part.NORTH_WEST));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART).shape;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos origin = context.getClickedPos();
        CollisionContext collision = CollisionContext.empty();
        for (Part part : PARTS) {
            BlockPos target = origin.offset(part.offset);
            BlockState partState = defaultBlockState().setValue(PART, part);
            if (!level.getWorldBorder().isWithinBounds(target) || !level.hasChunkAt(target)
                    || !level.getBlockState(target).canBeReplaced(context)
                    || !level.isUnobstructed(partState, target, collision)) {
                return null;
            }
        }
        return defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            for (Part part : PARTS) {
                if (part != Part.NORTH_WEST) {
                    level.setBlock(pos.offset(part.offset), state.setValue(PART, part), UPDATE_ALL);
                }
            }
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == Part.NORTH_WEST ? ModBlockEntityTypes.KIMJANG_BASIN.get().create(pos, state) : null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hit) {
        if (held.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide && level.getBlockEntity(origin(state, pos)) instanceof KimjangBasinBlockEntity basin) {
            basin.insert(player, hand, held);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(origin(state, pos)) instanceof KimjangBasinBlockEntity basin) {
            basin.use(player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!replacement.is(this)) {
            if (!level.isClientSide) {
                if (level.getBlockEntity(pos) instanceof KimjangBasinBlockEntity basin) {
                    Containers.dropContents(level, pos, basin.takeContents());
                }
                BlockPos origin = origin(state, pos);
                for (Part part : PARTS) {
                    BlockPos target = origin.offset(part.offset);
                    BlockState neighbor = level.getBlockState(target);
                    if (neighbor.is(this) && neighbor.getValue(PART) == part) {
                        level.removeBlock(target, false);
                    }
                }
            }
            super.onRemove(state, level, pos, replacement, moving);
        }
    }

    private static BlockPos origin(BlockState state, BlockPos pos) {
        return pos.subtract(state.getValue(PART).offset);
    }

    public enum Part implements StringRepresentable {
        NORTH_WEST("north_west", 0, 0),
        NORTH_EAST("north_east", 1, 0),
        SOUTH_EAST("south_east", 1, 1),
        SOUTH_WEST("south_west", 0, 1);

        private final String name;
        private final BlockPos offset;
        private final VoxelShape shape;

        Part(String name, int x, int z) {
            this.name = name;
            this.offset = new BlockPos(x, 0, z);
            int minX = x == 0 ? 1 : 0;
            int maxX = x == 0 ? 16 : 15;
            int minZ = z == 0 ? 1 : 0;
            int maxZ = z == 0 ? 16 : 15;
            this.shape = Shapes.or(
                    box(minX, 0, minZ, maxX, 2, maxZ),
                    box(x == 0 ? 1 : 13, 2, minZ, x == 0 ? 3 : 15, 7, maxZ),
                    box(minX, 2, z == 0 ? 1 : 13, maxX, 7, z == 0 ? 3 : 15));
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
