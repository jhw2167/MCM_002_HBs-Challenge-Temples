package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;

public class ChallengeLever extends LeverBlock {
    public ChallengeLever() {
        super(BlockBehaviour.Properties.copy(Blocks.LEVER)
            .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
            .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES));
    }
}
