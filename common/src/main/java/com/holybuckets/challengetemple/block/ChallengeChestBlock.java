package com.holybuckets.challengetemple.block;

import com.holybuckets.challengetemple.block.be.ChallengeChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

public class ChallengeChestBlock extends ChestBlock {
    public ChallengeChestBlock() {
        super(Properties.copy(ModBlocks.challengeBrick)
            .destroyTime(-1f)
            .explosionResistance(3600000f)
            .pushReaction(PushReaction.IGNORE), 
            () -> null); // BlockEntityType will be set after registration
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChallengeChestBlockEntity(pos, state);
    }
}
