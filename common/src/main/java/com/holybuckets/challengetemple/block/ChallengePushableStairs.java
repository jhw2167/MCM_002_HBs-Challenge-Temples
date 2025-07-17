package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;

public class ChallengePushableStairs extends StairBlock {
    public ChallengePushableStairs() {
        super(Blocks.STONE_BRICK_STAIRS.defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICK_STAIRS)
            .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH_MINEABLE)
            .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES)
                .pushReaction(PushReaction.NORMAL));
    }
}
