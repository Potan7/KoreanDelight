package com.potan.koreandelight.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Supplier;

/**
 * 액체 블록 등록 시 유체가 아직 등록되지 않았을 수 있으므로,
 * 유체(Fluid)를 Supplier 형태로 받아 지연 접근할 수 있도록 하는 블록 클래스입니다.
 */
public class ModLiquidBlock extends LiquidBlock {
    private final Supplier<? extends FlowingFluid> fluidSupplier;

    public ModLiquidBlock(Supplier<? extends FlowingFluid> fluidSupplier, BlockBehaviour.Properties properties) {
        // 부모 생성자에는 null 대신 Fluids.WATER를 전달하여 1.21.1에서의 NPE를 방지합니다.
        // 1.21.1 버전의 LiquidBlock은 생성 시 유체를 바로 참조하기 때문입니다.
        super(Fluids.WATER, properties);
        this.fluidSupplier = fluidSupplier;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return this.fluidSupplier.get().getSource(false);
    }

    @Override
    public ItemStack pickupBlock(Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        if (state.getValue(LEVEL) == 0) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            return new ItemStack(this.fluidSupplier.get().getBucket());
        } else {
            return ItemStack.EMPTY;
        }
    }
}
