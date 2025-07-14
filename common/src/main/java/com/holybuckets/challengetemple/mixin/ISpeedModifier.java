package com.holybuckets.challengetemple.mixin;

import com.holybuckets.challengetemple.block.ModBlocks;
import net.minecraft.world.level.block.state.BlockState;

public interface ISpeedModifier {

    default float modifySpeed(float original, BlockState state) {
        if (ModBlocks.MINEABLE.contains(state)) {
            if (original > 1.0f) {
                return ModBlocks.MINEABLE_SPEED;
            }
        }
        return original;
    }
}

