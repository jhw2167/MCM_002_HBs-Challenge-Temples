package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeBed extends RespawnAnchorBlock {
    public ChallengeBed() {
        super(Properties.copy(Blocks.RESPAWN_ANCHOR)
            .destroyTime(-1f)
            .explosionResistance(3600000f)
            .pushReaction(PushReaction.IGNORE)
            .requiresCorrectToolForDrops());
    }

    @Override
    public boolean canBeCharged(BlockState state) {
        return state.getValue(CHARGE) < 4;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, java.util.Random random) {
        // Override to prevent particle effects if desired
    }
}
