package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public class RandomBrick extends Block {
    public RandomBrick() {
        super(PROPERTIES);
    }

    static BlockBehaviour.Properties PROPERTIES = BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS)
        .destroyTime(-1f)
        .explosionResistance(3600000f)
        .pushReaction(PushReaction.IGNORE);
}
