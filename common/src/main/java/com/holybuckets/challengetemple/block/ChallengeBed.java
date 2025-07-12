package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeBed extends Block {
    public ChallengeBed() {
        super(Properties.copy(Blocks.RESPAWN_ANCHOR)
            .destroyTime(-1f)
            .explosionResistance(3600000f)
            .pushReaction(PushReaction.IGNORE)
            .requiresCorrectToolForDrops());
    }
}
