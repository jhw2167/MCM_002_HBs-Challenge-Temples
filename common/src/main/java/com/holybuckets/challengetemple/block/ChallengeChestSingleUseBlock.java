package com.holybuckets.challengetemple.block;

import com.holybuckets.challengetemple.block.be.ChallengeChestBlockEntity;
import com.holybuckets.challengetemple.block.be.ChallengeSingleUseChestBlockEntity;
import com.holybuckets.challengetemple.block.be.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

public class ChallengeChestSingleUseBlock extends ChestBlock {
    public ChallengeChestSingleUseBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.CHEST)
            .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
            .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES)
            .pushReaction(PushReaction.IGNORE),
            () -> ModBlockEntities.challengeSingleUseChest.get());
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChallengeSingleUseChestBlockEntity(pos, state);
    }
}
