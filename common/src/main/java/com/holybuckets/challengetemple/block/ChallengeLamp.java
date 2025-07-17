package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ChallengeLamp extends Block {
    public ChallengeLamp() {
        super(BlockBehaviour.Properties.copy(Blocks.REDSTONE_LAMP)
            .destroyTime(-1f)
            .explosionResistance(3600000f)
            .lightLevel((state) -> 15)); // Always lit
    }
}
