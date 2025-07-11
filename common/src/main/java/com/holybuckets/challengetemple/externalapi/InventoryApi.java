package com.holybuckets.challengetemple.externalapi;

import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Map;

/**
 * API for managing player inventory snapshots
 */
public interface InventoryApi {


    void setChallengeLevel(ServerLevel level);
    void setInstance(InventoryApi ai);

    void initConfig();

    /**
     * Creates a grave at the specified location containing the player's current inventory
     *
     * @param player   The player whose inventory to store in the grave
     * @param position The position where the grave should be created
     * @return true if grave creation was successful
     */
    boolean createGrave(ServerPlayer player, BlockPos position);

    /**
     * Restores a player's inventory from their last snapshot
     *
     * @param player The player whose inventory to restore
     * @return true if restoration was successful, false if no snapshot exists
     */
    boolean returnInventory(ServerPlayer player, BlockPos gravePos);

    void clearUnusedGraves(MinecraftServer server);

    Map<ServerPlayer, BlockPos> getGravePos();

    /**
     * Sets the positions of protected graves that should not be cleared
     * @param positions List of BlockPos representing protected grave locations
     */
    void setProtectedGravePos(List<BlockPos> positions);
}