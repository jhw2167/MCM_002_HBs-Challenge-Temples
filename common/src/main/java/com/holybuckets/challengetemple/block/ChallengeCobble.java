package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ChallengeCobble extends Block {
    public ChallengeCobble() {
        super(PROPERTIES);
    }

    static Properties PROPERTIES = Properties.copy(Blocks.COBBLESTONE)
        .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH);

}
