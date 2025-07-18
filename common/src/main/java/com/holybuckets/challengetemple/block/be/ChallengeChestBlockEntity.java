package com.holybuckets.challengetemple.block.be;

import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.menu.ChallengeChestCountingMenu;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ChallengeChestBlockEntity extends ChestBlockEntity implements LidBlockEntity {
    public ChallengeChestBlockEntity(BlockPos pos, BlockState state) {
        super( ModBlockEntities.challengeChest.get(), pos, state);
    }
}
