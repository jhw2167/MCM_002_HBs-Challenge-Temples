package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ChallengeStone extends Block {
    public ChallengeStone() {
        super(PROPERTIES);
    }

    static Properties PROPERTIES = Properties.copy(Blocks.STONE)
        .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH);

}
