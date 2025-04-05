package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeBrickSlab extends SlabBlock {
    public ChallengeBrickSlab() {
        super(PROPERTIES);
    }

    static Properties BEDROCK_PROPERTIES = Properties.copy(Blocks.BEDROCK);
    /*
        .destroyTime(-1f)
        .explosionResistance(3600000f)
        .pushReaction(PushReaction.IGNORE);
     */

    static Properties PROPERTIES = Properties.copy(Blocks.STONE_BRICK_SLAB)
        .destroyTime(-1f)
        .explosionResistance(3600000f)
        .pushReaction(PushReaction.IGNORE);



    @Override
    public int getLightBlock(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return 0;
    }
}
