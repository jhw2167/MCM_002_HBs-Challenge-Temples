package com.holybuckets.challengetemple.externalapi;

import eu.pb4.graves.GravesApi;
import eu.pb4.graves.grave.Grave;
import eu.pb4.graves.grave.GraveInventoryMask;
import eu.pb4.graves.grave.GraveManager;
import eu.pb4.graves.grave.GraveType;
import eu.pb4.graves.grave.PositionedItemStack;
import eu.pb4.graves.mixin.PlayerEntityAccessor;
import eu.pb4.graves.other.Location;
import eu.pb4.graves.other.VanillaInventoryMask;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * API for managing player inventory snapshots using UniversalGraves functionality
 */
public class InventoryAPI {
    private static final Map<UUID, SnapshotData> playerSnapshots = new HashMap<>();

    private static class SnapshotData {
        final Map<Integer, ItemStack> items = new HashMap<>();
        final long timestamp;

        SnapshotData() {
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Creates a grave at the specified location containing the player's current inventory
     * @param player The player whose inventory to store in the grave
     * @param position The position where the grave should be created
     * @return true if grave creation was successful
     */
    public static boolean createGrave(ServerPlayerEntity player, BlockPos position) {
        try {
            // Collect items from player inventory
            List<PositionedItemStack> items = new ArrayList<>();
            VanillaInventoryMask.INSTANCE.addToGrave(player, new GraveInventoryMask.ItemConsumer() {
                @Override
                public void addItem(ItemStack stack, int slot, NbtElement nbtElement, Identifier... tags) {
                    if (GravesApi.canAddItem(player, stack)) {
                        items.add(new PositionedItemStack(stack.copy(), slot, VanillaInventoryMask.INSTANCE, nbtElement, Set.of(tags)));
                    }
                }
            });

            // Create new grave with collected items
            Grave grave = new Grave(
                GraveManager.INSTANCE.requestId(),
                player.getGameProfile(),
                player.getDataTracker().get(PlayerEntityAccessor.getPLAYER_MODEL_PARTS()),
                player.getMainArm(),
                position,
                player.getWorld().getRegistryKey().getValue(),
                GraveType.BLOCK,
                System.currentTimeMillis() / 1000,
                GraveManager.INSTANCE.getCurrentGameTime(),
                player.experienceLevel,
                Text.literal("Manual grave creation"),
                Collections.singleton(player.getUuid()),
                items,
                true,
                (int)(player.getWorld().getTime() / 24000L)
            );

            // Add grave to manager and clear player inventory
            GraveManager.INSTANCE.add(grave);
            clearInventory(player);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Restores a player's inventory from their last snapshot
     * @param player The player whose inventory to restore
     * @return true if restoration was successful, false if no snapshot exists
     */
    public static boolean restoreSnapshot(ServerPlayerEntity player) {
        SnapshotData snapshot = playerSnapshots.get(player.getUuid());
        if (snapshot == null) {
            return false;
        }

        // Clear current inventory first
        player.getInventory().clear();

        // Restore items using VanillaInventoryMask
        for (Map.Entry<Integer, ItemStack> entry : snapshot.items.entrySet()) {
            VanillaInventoryMask.INSTANCE.moveToPlayerExactly(
                player,
                entry.getValue().copy(),
                entry.getKey(),
                null
            );
        }

        return true;
    }

    /**
     * Clears a player's current inventory
     * @param player The player whose inventory to clear
     */
    public static void clearInventory(ServerPlayerEntity player) {
        player.getInventory().clear();
    }

    /**
     * Gets the timestamp of when a player's snapshot was created
     * @param player The player to check
     * @return The timestamp in milliseconds, or -1 if no snapshot exists
     */
    public static long getSnapshotTimestamp(ServerPlayerEntity player) {
        SnapshotData snapshot = playerSnapshots.get(player.getUuid());
        return snapshot != null ? snapshot.timestamp : -1;
    }

    /**
     * Removes a player's snapshot from storage
     * @param player The player whose snapshot to remove
     */
    public static void removeSnapshot(ServerPlayerEntity player) {
        playerSnapshots.remove(player.getUuid());
    }
}
