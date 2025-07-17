package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeCountingChestBlock extends ChestBlock {
    public ChallengeCountingChestBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.CHEST)
            .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
            .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES)
            .pushReaction(PushReaction.IGNORE),
            () -> BlockEntityType.CHEST);
    }
}
