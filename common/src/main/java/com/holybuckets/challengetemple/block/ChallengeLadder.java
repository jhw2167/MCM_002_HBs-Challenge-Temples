package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class ChallengeLadder extends LadderBlock {
    public ChallengeLadder() {
        super(BlockBehaviour.Properties.of(Material.DECORATION)
                .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
                .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES)
                .sound(SoundType.LADDER)
                .noOcclusion());
    }
}
