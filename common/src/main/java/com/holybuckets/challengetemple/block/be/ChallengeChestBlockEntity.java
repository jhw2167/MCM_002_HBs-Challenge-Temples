package com.holybuckets.challengetemple.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ChallengeChestBlockEntity extends ChestBlockEntity {
    public ChallengeChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CHALLENGE_CHEST.get(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-1, 0, -1), worldPosition.offset(2, 2, 2));
    }
}
