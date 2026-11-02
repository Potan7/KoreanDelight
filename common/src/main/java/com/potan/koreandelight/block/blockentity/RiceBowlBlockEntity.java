package com.potan.koreandelight.block.blockentity;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.custom.RiceBowlBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RiceBowlBlockEntity extends BlockEntity {
    private Vec3 placementOffset = Vec3.ZERO;

    public RiceBowlBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.RICE_BOWL.get(), pos, state);
    }

    public Vec3 getPlacementOffset() {
        return placementOffset;
    }

    public void setPlacementLocation(Vec3 hit) {
        placementOffset = offsetFromClick(worldPosition, hit, getCenteredShape());
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public static Vec3 offsetFromClick(BlockPos pos, Vec3 hit) {
        return offsetFromClick(pos, hit, RiceBowlBlock.DEFAULT_SHAPE);
    }

    public static Vec3 offsetFromClick(BlockPos pos, Vec3 hit, VoxelShape shape) {
        return clampOffset(hit.x - pos.getX() - 0.5, hit.z - pos.getZ() - 0.5, shape);
    }

    private VoxelShape getCenteredShape() {
        return getBlockState().getBlock() instanceof RiceBowlBlock bowl
                ? bowl.getCenteredShape() : RiceBowlBlock.DEFAULT_SHAPE;
    }

    private static Vec3 clampOffset(double x, double z, VoxelShape shape) {
        return new Vec3(Double.isFinite(x) ? Mth.clamp(x, -shape.min(Direction.Axis.X), 1 - shape.max(Direction.Axis.X)) : 0, 0,
                Double.isFinite(z) ? Mth.clamp(z, -shape.min(Direction.Axis.Z), 1 - shape.max(Direction.Axis.Z)) : 0);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        placementOffset = clampOffset(tag.getDouble("OffsetX"), tag.getDouble("OffsetZ"), getCenteredShape());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putDouble("OffsetX", placementOffset.x);
        tag.putDouble("OffsetZ", placementOffset.z);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
