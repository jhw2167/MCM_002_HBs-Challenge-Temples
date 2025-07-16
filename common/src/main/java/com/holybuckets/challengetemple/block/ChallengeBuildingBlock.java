package com.holybuckets.challengetemple.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.DyeColor;

public class ChallengeBuildingBlock extends Block {
    private final DyeColor color;

    public ChallengeBuildingBlock(DyeColor color) {
        super(BlockBehaviour.Properties.copy(Blocks.WHITE_CONCRETE)
            .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH_MINEABLE)
            .requiresCorrectToolForDrops());
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }
}
