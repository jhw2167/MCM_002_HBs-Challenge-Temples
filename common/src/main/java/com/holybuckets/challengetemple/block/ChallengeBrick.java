package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeBrick extends Block {
    public ChallengeBrick() {
        super(PROPERTIES);
    }

    static BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)
        .destroyTime(-1f)
        .explosionResistance(3600000f)
        .pushReaction(PushReaction.IGNORE);



    @Override
    public int getLightBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return 0;
    }
}
