package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeLadder extends LadderBlock {
    public ChallengeLadder() {
        super(Properties.copy(Blocks.LADDER)
                .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
                .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES)
                .sound(SoundType.LADDER)
                .pushReaction(PushReaction.NORMAL)  // Prevents the ladder from being pushed by pistons
                .noCollission()
                .noOcclusion()
                .dynamicShape());  // This helps with collision detection
    }

    boolean isClimbable() {
        return true;  // Ensure the ladder is climbable
    }
}
