package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class ChallengeChestCountingBlock extends ChestBlock {
    public ChallengeChestCountingBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.CHEST)
            .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
            .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES)
            .pushReaction(PushReaction.IGNORE),
            () -> BlockEntityType.CHEST);
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
        InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) return InteractionResult.SUCCESS;

        return super.use(state, level, pos, player, hand, hit);
    }
}
