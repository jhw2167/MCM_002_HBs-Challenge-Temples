package com.holybuckets.challengetemple.block.be;

import com.holybuckets.challengetemple.block.ChallengeChestCountingBlock;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.menu.ChallengeChestCountingMenu;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

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

    @Override
    protected void signalOpenCount(Level $$0, BlockPos $$1, BlockState $$2, int $$3, int $$4) {
        super.signalOpenCount($$0, $$1, $$2, $$3, $$4);
        if ($$3 != $$4) {
            Block $$5 = $$2.getBlock();
            $$0.updateNeighborsAt($$1, $$5);
            $$0.updateNeighborsAt($$1.below(), $$5);
        }

    }

    @Override
    public void stopOpen(Player $$0) {
        super.stopOpen($$0);
        Block $$5 = this.getBlockState().getBlock();
        this.level.updateNeighborsAt(this.getBlockPos(), $$5);
        this.level.updateNeighborsAt(this.getBlockPos().below(), $$5);
    }

    @Override
    public int getContainerSize() {
        return 27; // 9 restricted slots + 18 normal slots
    }

    //Emit redstone signal when total items in restricted slots match items in normal slots
    public boolean isRestrictedSlotMatchingNormalSlots() {
        // Count matching items between restricted (slots 0-8) and normal (slots 9+)
        Map<Item, Integer> restrictedCounts = new HashMap<>();
        Map<Item, Integer> normalCounts = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.getItem(i);
            if (!stack.isEmpty()) {
                restrictedCounts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }

        for (int i = 9; i < this.getContainerSize(); i++) {
            ItemStack stack = this.getItem(i);
            if (!stack.isEmpty()) {
                normalCounts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }

        //if there is at least as many restricted items, return true
        for(Map.Entry<Item, Integer> entry : restrictedCounts.entrySet()) {
            Item item = entry.getKey();
            int restrictedCount = entry.getValue();
            int normalCount = normalCounts.getOrDefault(item, 0);
            if (restrictedCount > normalCount) {
                return false; // Mismatch found
            }
        }

        return true;
    }

    public BalmMenuProvider getMenuProvider() {
        return new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("block.hbs_challenge_temple.challenge_chest_counting");
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                ChallengeChestCountingBlockEntity.this.setLevel(player.level());
                return new ChallengeChestCountingMenu(syncId, playerInventory, ChallengeChestCountingBlockEntity.this);
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                buf.writeBlockPos(worldPosition);
            }
        };
    }


}
