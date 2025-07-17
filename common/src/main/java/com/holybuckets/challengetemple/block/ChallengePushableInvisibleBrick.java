package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ChallengePushableInvisibleBrick extends GlassBlock {
    public ChallengePushableInvisibleBrick() {
        super(PROPERTIES);
    }

    static Properties PROPERTIES = Properties.copy(Blocks.GLASS)
        .destroyTime(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
        .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES_BLASTABLE)
        .pushReaction(PushReaction.NORMAL);

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.INVISIBLE;
    }
}
