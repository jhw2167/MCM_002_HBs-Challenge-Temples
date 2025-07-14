package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ChallengeLadder extends LadderBlock {
    public ChallengeLadder() {
        super(Properties.copy(Blocks.LADDER)
                .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
                .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES)
                .sound(SoundType.LADDER)
                .noCollission()
                .noOcclusion()
                .dynamicShape());  // This helps with collision detection
    }
}
