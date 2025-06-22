package com.holybuckets.challengetemple.portal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

/**
 * API for managing player inventory snapshots
 */
public interface InventoryAPI {
    /**
     * Creates a grave at the specified location containing the player's current inventory
     * @param player The player whose inventory to store in the grave
     * @param position The position where the grave should be created
     * @return true if grave creation was successful
     */
    boolean createGrave(ServerPlayer player, BlockPos position);

    /**
     * Restores a player's inventory from their last snapshot
     * @param player The player whose inventory to restore
     * @return true if restoration was successful, false if no snapshot exists
     */
    boolean restoreSnapshot(ServerPlayer player);

    /**
     * Clears a player's current inventory
     * @param player The player whose inventory to clear
     */
    void clearInventory(ServerPlayer player);

    /**
     * Gets the timestamp of when a player's snapshot was created
     * @param player The player to check
     * @return The timestamp in milliseconds, or -1 if no snapshot exists
     */
    long getSnapshotTimestamp(ServerPlayer player);

    /**
     * Removes a player's snapshot from storage
     * @param player The player whose snapshot to remove
     */
    void removeSnapshot(ServerPlayer player);
}
