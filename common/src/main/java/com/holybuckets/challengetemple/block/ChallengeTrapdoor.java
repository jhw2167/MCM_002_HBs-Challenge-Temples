package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ChallengeTrapdoor extends TrapDoorBlock {
    public ChallengeTrapdoor() {
        super(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR)
            .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
            .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES)
            .noOcclusion(),
            BlockSetType.OAK);
    }
}
