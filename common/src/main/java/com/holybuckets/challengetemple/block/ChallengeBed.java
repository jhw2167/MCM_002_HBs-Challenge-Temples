package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeBed extends Block {
    public ChallengeBed() {
        super(Properties.copy(Blocks.STONE_BRICKS)
            .destroyTime(-1f)
            .explosionResistance(3600000f)
            .pushReaction(PushReaction.IGNORE)
            .requiresCorrectToolForDrops());
    }

    @Override
    public int getLightBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return 0;
    }
}
