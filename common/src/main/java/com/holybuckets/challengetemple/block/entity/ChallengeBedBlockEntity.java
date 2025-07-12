package com.holybuckets.challengetemple.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChallengeBedBlockEntity extends BedBlockEntity {
    public ChallengeBedBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        setColor(DyeColor.RED);
    }
}
