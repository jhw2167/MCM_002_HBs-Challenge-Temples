package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeWood extends Block {
    public ChallengeWood() {
        super(PROPERTIES);
    }

    static Properties PROPERTIES = Properties.copy(Blocks.OAK_PLANKS)
        .destroyTime(-1f);


    @Override
    public int getLightBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return 0;
    }
}
