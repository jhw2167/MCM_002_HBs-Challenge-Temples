package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ChallengePushableBrick extends Block {
    public ChallengePushableBrick() {
        super(PROPERTIES);
    }

    static BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)
        .destroyTime(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
        .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES)
        .pushReaction(PushReaction.NORMAL);

    @Override
    public int getLightBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return 0;
    }
}
