package com.holybuckets.challengetemple.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ChallengeChestBlockEntity extends ChestBlockEntity implements LidBlockEntity {
    public ChallengeChestBlockEntity(BlockPos pos, BlockState state) {
        super( ModBlockEntities.challengeChest.get(), pos, state);
    }
}
