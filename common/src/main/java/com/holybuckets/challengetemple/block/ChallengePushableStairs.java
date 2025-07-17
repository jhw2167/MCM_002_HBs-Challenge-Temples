package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;

public class ChallengePushableStairs extends StairBlock {
    public ChallengePushableStairs() {
        super(Blocks.STONE_BRICK_STAIRS.defaultBlockState(),
            BlockBehaviour.Properties.copy(Blocks.STONE_BRICK_STAIRS)
                .destroyTime(-1f)
                .explosionResistance(3600000f)
                .pushReaction(PushReaction.NORMAL));
    }
}
