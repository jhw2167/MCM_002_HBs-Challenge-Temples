package com.holybuckets.challengetemple.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChallengeSingleUseChestBlockEntity extends ChestBlockEntity {
    private boolean hasBeenAccessed = false;

    public ChallengeSingleUseChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.challengeSingleUseChest.get(), pos, state);
    }

    public boolean hasBeenAccessed() {
        return hasBeenAccessed;
    }

    public void setAccessed() {
        hasBeenAccessed = true;
    }
}
