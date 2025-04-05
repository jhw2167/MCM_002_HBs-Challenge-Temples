package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ChallengeBrickBlock extends Block {
    public ChallengeBrickBlock() {
        super(CHALLENGE_BRICK_PROPERTIES);
    }

    static BlockBehaviour.Properties CHALLENGE_BRICK_PROPERTIES = BlockBehaviour.Properties.copy(Blocks.BEDROCK)
        .sound(SoundType.STONE);
}
