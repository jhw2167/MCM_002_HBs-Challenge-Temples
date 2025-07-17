package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeFence extends FenceBlock {
    public ChallengeFence() {
        super(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE)
            .destroyTime(-1f)
            .explosionResistance(3600000f)
            .pushReaction(PushReaction.IGNORE));
    }
}
