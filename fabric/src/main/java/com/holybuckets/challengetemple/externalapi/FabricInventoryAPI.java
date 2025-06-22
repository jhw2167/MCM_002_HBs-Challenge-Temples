package com.holybuckets.challengetemple.externalapi;

import eu.pb4.graves.grave.Grave;
import eu.pb4.graves.grave.GraveManager;
import eu.pb4.graves.grave.GraveType;
import eu.pb4.graves.grave.PositionedItemStack;
import eu.pb4.graves.mixin.PlayerEntityAccessor;
import eu.pb4.graves.other.VanillaInventoryMask;

import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * API for managing player inventory snapshots using UniversalGraves functionality
 */
public class FabricInventoryAPI {
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
    public static boolean createGrave(ServerPlayer player, BlockPos position) {
        try {
            // Collect items from player inventory
            List<PositionedItemStack> items = new ArrayList<>();

            // Create new grave with collected items
            Grave grave = new Grave(
                GraveManager.INSTANCE.requestId(),
                player.getGameProfile(),
                player.getEntityData().get(PlayerEntityAccessor.getPLAYER_MODEL_PARTS()), // verify accessor
                player.getMainArm(),
                position,
                player.level().dimension().location(),   // was getRegistryKey().getValue()
                GraveType.BLOCK,
                System.currentTimeMillis() / 1000,
                GraveManager.INSTANCE.getCurrentGameTime(),
                player.experienceLevel,
                Component.literal("Manual grave creation"),  // was Text.literal
                Collections.singleton(player.getUUID()),     // was getUuid()
                items,
                true,
                (int)(player.level().getGameTime() / 24000L)  // was player.getWorld().getTime()
            );


            // Add grave to manager and clear player inventory
            GraveManager.INSTANCE.add(grave);
            //clearInventory(player);

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
    public static boolean restoreSnapshot(ServerPlayer player) {
        SnapshotData snapshot = playerSnapshots.get(player.getUUID());
        if (snapshot == null) {
            return false;
        }

        // Clear current inventory first
        //player.getInventory().clear();

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
    public static void clearInventory(ServerPlayer player) {
        player.getInventory().clearContent();
    }

    /**
     * Gets the timestamp of when a player's snapshot was created
     * @param player The player to check
     * @return The timestamp in milliseconds, or -1 if no snapshot exists
     */
    public static long getSnapshotTimestamp(ServerPlayer player) {
        SnapshotData snapshot = playerSnapshots.get(player.getUUID());
        return snapshot != null ? snapshot.timestamp : -1;
    }

    /**
     * Removes a player's snapshot from storage
     * @param player The player whose snapshot to remove
     */
    public static void removeSnapshot(ServerPlayer player) {
        playerSnapshots.remove(player.getUUID() );
    }
}
