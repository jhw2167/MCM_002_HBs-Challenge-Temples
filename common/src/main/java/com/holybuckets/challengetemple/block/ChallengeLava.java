package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluids;

public class ChallengeLava extends LiquidBlock {
    public ChallengeLava() {
        super(Fluids.FLOWING_LAVA , BlockBehaviour.Properties.copy(Blocks.LAVA)
            .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
            .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES)
            .lightLevel((state) -> 15)
            .noLootTable());
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!entity.fireImmune()) {
            entity.hurt(level.damageSources().lava(), 10.0F); // 2x normal lava damage
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return false; // Disable random ticking to prevent fire spread
    }
}
