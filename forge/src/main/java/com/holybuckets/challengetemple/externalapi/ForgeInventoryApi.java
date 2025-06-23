package com.holybuckets.challengetemple.externalapi;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import  de.maxhenkel.corpse.*;
import java.util.List;
import java.util.Map;

/**
 * Forge implementation of inventory management API
 */
public class ForgeInventoryApi implements InventoryApi {

    private static ForgeInventoryApi INSTANCE;

    @Override
    public void setInstance(InventoryApi ai) {
        INSTANCE = (ForgeInventoryApi) ai;
    }

    public static ForgeInventoryApi getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean createGrave(ServerPlayer player, BlockPos position) {
        // TODO: Implement with appropriate Forge grave mod

        return true;
    }

    @Override
    public boolean returnInventory(ServerPlayer player, BlockPos gravePos) {
        // TODO: Implement inventory restoration
        return false;
    }

    @Override
    public void clearUnusedGraves(MinecraftServer server) {

    }

    @Override
    public Map<ServerPlayer, BlockPos> getGravePos() {
        return Map.of();
    }

    @Override
    public void setProtectedGravePos(List<BlockPos> positions) {

    }


}
package com.holybuckets.challengetemple.externalapi;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;

/**
 * Forge implementation of inventory management API using Corpse mod
 */
public class ForgeInventoryApi implements InventoryApi {

    private static ForgeInventoryApi INSTANCE;
    private final Map<ServerPlayer, BlockPos> playerGraves = new HashMap<>();
    private List<BlockPos> protectedGraves = new ArrayList<>();

    @Override
    public void setInstance(InventoryApi api) {
        INSTANCE = (ForgeInventoryApi) api;
    }

    public static ForgeInventoryApi getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean createGrave(ServerPlayer player, BlockPos position) {
        try {
            // TODO: Implement with Corpse mod API
            // For now just track the position
            playerGraves.put(player, position);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean returnInventory(ServerPlayer player, BlockPos gravePos) {
        try {
            // TODO: Implement inventory restoration using Corpse mod
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void clearUnusedGraves(MinecraftServer server) {
        // Remove graves that aren't in the protected list
        playerGraves.entrySet().removeIf(entry -> 
            !protectedGraves.contains(entry.getValue()));
    }

    @Override
    public Map<ServerPlayer, BlockPos> getGravePos() {
        return Collections.unmodifiableMap(playerGraves);
    }

    @Override
    public void setProtectedGravePos(List<BlockPos> positions) {
        this.protectedGraves = new ArrayList<>(positions);
    }
}
