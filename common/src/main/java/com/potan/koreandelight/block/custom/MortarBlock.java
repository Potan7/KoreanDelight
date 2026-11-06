package com.potan.koreandelight.block.custom;

import com.potan.koreandelight.block.ModBlockEntityTypes;
import com.potan.koreandelight.block.blockentity.MortarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MortarBlock extends Block implements EntityBlock {
    public MortarBlock(Properties properties) { super(properties); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return ModBlockEntityTypes.MORTAR.get().create(pos, state); }
    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Block.box(2, 0, 2, 14, 8, 14); }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.mayBuild()) return InteractionResult.PASS;
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
            if (mortar.isFinished() || player.isShiftKeyDown()) give(player, mortar.take());
            else strike(level, pos, mortar, 2);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (held.isEmpty() || !player.mayBuild()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
            if (mortar.isFinished()) give(player, mortar.take());
            else { int inserted = mortar.insert(held); if (!player.isCreative()) held.shrink(inserted); }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    private static void give(Player player, ItemStack stack) {
        if (!stack.isEmpty() && !player.getInventory().add(stack)) player.drop(stack, false);
    }
    private static void strike(Level level, BlockPos pos, MortarBlockEntity mortar, int amount) {
        ItemStack particles = mortar.getStack().copyWithCount(1);
        if (mortar.strike(amount)) {
            level.playSound(null, pos, SoundEvents.STONE_HIT, SoundSource.BLOCKS, 0.8F, 0.8F);
            if (level instanceof ServerLevel server) server.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, particles), pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 6, 0.12, 0.1, 0.12, 0.03);
        }
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
                ItemStack stack = mortar.take();
                if (!stack.isEmpty()) Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
            super.onRemove(state, level, pos, newState, moving);
        }
    }
}
