package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeFenceGate extends FenceGateBlock {
    public ChallengeFenceGate() {
        super(BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE)
            .destroyTime(-1f)
            .explosionResistance(3600000f)
            .pushReaction(PushReaction.IGNORE),
            WoodType.OAK);
    }
}
