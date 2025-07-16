package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ChallengeDoor extends DoorBlock {
    public ChallengeDoor() {
        super(BlockBehaviour.Properties.copy(Blocks.IRON_DOOR)
                .strength(0f)
                .noOcclusion(),
            BlockSetType.IRON);
    }
}
