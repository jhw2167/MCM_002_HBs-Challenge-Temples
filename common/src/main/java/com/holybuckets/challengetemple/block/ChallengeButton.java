package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ChallengeButton extends ButtonBlock {
    public ChallengeButton() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE_BUTTON)
            .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
            .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES),
            BlockSetType.STONE,
            30, // activation time in ticks
            false); // arrow can trigger
    }
}
