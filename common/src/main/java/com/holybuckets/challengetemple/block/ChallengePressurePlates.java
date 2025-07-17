package com.holybuckets.challengetemple.block;

import com.holybuckets.challengetemple.core.ManagedChallenger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ChallengePressurePlates {
    
    public static class ChallengeLightPlate extends PressurePlateBlock {
        public ChallengeLightPlate() {
            super(Sensitivity.EVERYTHING, 
                BlockBehaviour.Properties.copy(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE)
                    .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
                    .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES),
                BlockSetType.GOLD);
        }
    }

    public static class ChallengeClearingPlate extends PressurePlateBlock {
        public ChallengeClearingPlate() {
            super(Sensitivity.MOBS,
                BlockBehaviour.Properties.copy(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
                    .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
                    .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES),
                BlockSetType.IRON);
        }

        @Override
        protected int getSignalStrength(Level level, BlockPos pos) {
            int signal = super.getSignalStrength(level, pos);
            if (signal > 0) {
                ManagedChallenger.onChallengerClearingPlatePressed(level, pos);
            }
            return signal;
        }
    }

    public static class ChallengePressurePlate extends PressurePlateBlock {
        public ChallengePressurePlate() {
            super(Sensitivity.MOBS,
                BlockBehaviour.Properties.copy(Blocks.STONE_PRESSURE_PLATE)
                    .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
                    .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES),
                BlockSetType.STONE);
        }
    }
}
