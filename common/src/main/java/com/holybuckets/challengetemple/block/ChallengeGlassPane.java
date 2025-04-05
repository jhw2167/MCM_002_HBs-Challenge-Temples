package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class ChallengeGlassPane extends IronBarsBlock {
    public ChallengeGlassPane() {
        super(PROPERTIES);
    }

    static Properties PROPERTIES = Properties.copy(Blocks.GLASS_PANE)
        .destroyTime(-1f)
        .explosionResistance(3600000f)
        .pushReaction(PushReaction.IGNORE)
        .noOcclusion();
}
