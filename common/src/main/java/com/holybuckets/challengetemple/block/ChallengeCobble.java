package com.holybuckets.challengetemple.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ChallengeCobble extends Block {
    public ChallengeCobble() {
        super(PROPERTIES);
    }

    static Properties PROPERTIES = Properties.copy(Blocks.COBBLESTONE)
            .strength(ModBlocks.CHALLENGE_BLOCK_STRENGTH_MINEABLE)
            .requiresCorrectToolForDrops()
        .explosionResistance(ModBlocks.CHALLENGE_BLOCK_EXPL_RES_BLASTABLE );

    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        //nothing
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }



}
