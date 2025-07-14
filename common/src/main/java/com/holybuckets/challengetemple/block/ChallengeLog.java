package com.holybuckets.challengetemple.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class ChallengeLog extends RotatedPillarBlock {
    public ChallengeLog() {
        super(PROPERTIES);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    private static final Properties PROPERTIES = BlockBehaviour.Properties.copy(Blocks.SPRUCE_LOG)
        .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH)
        .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES_BLASTABLE)
        .requiresCorrectToolForDrops();

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        //nothing
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }



}
