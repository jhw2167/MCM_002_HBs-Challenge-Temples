package com.holybuckets.challengetemple.block.be;

import com.holybuckets.challengetemple.block.ChallengeChestCountingBlock;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.menu.ChallengeChestCountingMenu;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChallengeChestCountingBlockEntity extends ChestBlockEntity implements LidBlockEntity {

    public ChallengeChestCountingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.challengeCountingChest.get(), pos, state);
    }

    public ChallengeChestCountingBlockEntity(BlockPos pos) {
        this(pos, ModBlocks.challengeCountingChest.defaultBlockState());
    }

    public ChallengeChestCountingBlockEntity() {
        this(BlockPos.ZERO);
    }

    public BalmMenuProvider getMenuProvider() {
        return new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("block.hbs_challenge_temple.challenge_chest_counting");
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                ChallengeChestCountingBlockEntity be = new ChallengeChestCountingBlockEntity(player.getOnPos());
                be.setLevel(player.level());
                return new ChallengeChestCountingMenu(syncId, playerInventory, be);
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                buf.writeBlockPos(worldPosition);
            }
        };
    }


}
