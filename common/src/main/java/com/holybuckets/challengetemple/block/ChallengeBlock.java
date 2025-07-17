package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeBlock extends Block {

    public ChallengeBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)
            .destroyTime(-1f)
            .explosionResistance(3600000f)
            .pushReaction(PushReaction.IGNORE));
    }

}
