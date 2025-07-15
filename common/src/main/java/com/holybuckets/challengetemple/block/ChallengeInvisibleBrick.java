package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeInvisibleBrick extends GlassBlock {
    public ChallengeInvisibleBrick() {
        super(PROPERTIES);
    }

    static Properties PROPERTIES = Properties.copy(Blocks.GLASS)
        .destroyTime(-1f)
        .explosionResistance(3600000f)
        .pushReaction(PushReaction.IGNORE);

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.INVISIBLE;
    }

}
