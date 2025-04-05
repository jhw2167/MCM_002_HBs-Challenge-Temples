package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeGlowstone extends Block {
    public ChallengeGlowstone() {
        super(PROPERTIES);
    }

    static Properties PROPERTIES = Properties.copy(Blocks.GLOWSTONE)
        .destroyTime(-1f)
        .explosionResistance(3600000f)
        .pushReaction(PushReaction.IGNORE);

}
