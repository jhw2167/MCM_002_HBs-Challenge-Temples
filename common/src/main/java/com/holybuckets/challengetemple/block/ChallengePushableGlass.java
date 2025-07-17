package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ChallengePushableGlass extends GlassBlock {
    public ChallengePushableGlass() {
        super(PROPERTIES);
    }

    static Properties PROPERTIES = Properties.copy(Blocks.GLASS)
        .destroyTime(-1f)
        .explosionResistance(3600000f)
        .pushReaction(PushReaction.NORMAL);

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }
}
