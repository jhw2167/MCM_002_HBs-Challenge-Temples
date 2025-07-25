package com.holybuckets.challengetemple.block;

import com.holybuckets.challengetemple.block.be.ChallengeChestBlockEntity;
import com.holybuckets.challengetemple.block.be.ChallengeChestCountingBlockEntity;
import com.holybuckets.challengetemple.block.be.ModBlockEntities;
import net.blay09.mods.balm.api.Balm;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
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
            () -> ModBlockEntities.challengeCountingChest.get() );
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (!(blockEntity instanceof ChallengeChestCountingBlockEntity)) {
            return 0;
        }
        ChallengeChestCountingBlockEntity chestBe = (ChallengeChestCountingBlockEntity) blockEntity;
        return chestBe.isRestrictedSlotMatchingNormalSlots() ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$3 == Direction.UP ? $$0.getSignal($$1, $$2, $$3) : 0;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChallengeChestCountingBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit)
    {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if( blockEntity instanceof ChallengeChestCountingBlockEntity) {
                ChallengeChestCountingBlockEntity chestBe = (ChallengeChestCountingBlockEntity) blockEntity;
                MenuProvider menuProvider = chestBe.getMenuProvider();
                Balm.getNetworking().openMenu(player, menuProvider);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
