package com.potan.koreandelight.item;

import com.potan.koreandelight.block.blockentity.RiceBowlBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RiceBowlItem extends BlockItem {
    public RiceBowlItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.isSecondaryUseActive()) {
            return context.getClickedFace() == Direction.UP
                    ? place(new BlockPlaceContext(context)) : InteractionResult.FAIL;
        }
        InteractionResult result = use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
        return result == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : result;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        if (!super.placeBlock(context, state)) {
            return false;
        }
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof RiceBowlBlockEntity bowl) {
            bowl.setPlacementLocation(context.getClickLocation());
        }
        return true;
    }
}
