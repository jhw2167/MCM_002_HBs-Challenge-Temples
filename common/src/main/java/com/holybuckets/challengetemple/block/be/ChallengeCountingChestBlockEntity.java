package com.holybuckets.challengetemple.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChallengeCountingChestBlockEntity extends ChestBlockEntity {
    private int accessCount = 0;

    public ChallengeCountingChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.challengeCountingChest.get(), pos, state);
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void incrementAccessCount() {
        accessCount++;
    }
}
