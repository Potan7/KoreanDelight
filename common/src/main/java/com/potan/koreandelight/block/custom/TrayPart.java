package com.potan.koreandelight.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TrayPart implements StringRepresentable {
    NORTH_WEST("north_west"),
    NORTH_EAST("north_east"),
    SOUTH_WEST("south_west"),
    SOUTH_EAST("south_east");

    private final String name;
    TrayPart(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // 각 파트의 오프셋을 반환합니다.
    public BlockPos getOffset() {
        return switch (this) {
            case NORTH_WEST -> new BlockPos(0, 0, 0); // 마스터
            case NORTH_EAST -> new BlockPos(1, 0, 0);
            case SOUTH_WEST -> new BlockPos(0, 0, 1);
            case SOUTH_EAST -> new BlockPos(1, 0, 1);
        };
    }
}