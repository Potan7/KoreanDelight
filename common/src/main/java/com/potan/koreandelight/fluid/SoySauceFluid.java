package com.potan.koreandelight.fluid;

import com.potan.koreandelight.block.ModBlocks;
import com.potan.koreandelight.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * 간장(Soy Sauce) 액체의 공통 로직을 담은 클래스입니다.
 * 1.21.1 Multiloader의 'common' 모듈에서 플랫폼 독립적인 액체 로직을 정의합니다.
 */
public abstract class SoySauceFluid extends FlowingFluid {
    @Override
    public FluidType getFluidType() {
        return ModFluidTypes.SOY_SAUCE_FLUID_TYPE.get();
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_SOY_SAUCE.get();
    }

    @Override
    public Fluid getSource() {
        return ModFluids.SOURCE_SOY_SAUCE.get();
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.isSame(Fluids.WATER);
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 5;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return ModBlocks.SOY_SAUCE_BLOCK.get().defaultBlockState();
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == getSource() || fluid == getFlowing();
    }

    @Override
    public Item getBucket() {
        return ModItems.SOY_SAUCE_BUCKET.get();
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
    }

    @Override
    protected int getSlopeFindDistance(LevelReader level) {
        return 4;
    }

    @Override
    protected int getDropOff(LevelReader level) {
        return 1;
    }

    public static class Flowing extends SoySauceFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends SoySauceFluid {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
