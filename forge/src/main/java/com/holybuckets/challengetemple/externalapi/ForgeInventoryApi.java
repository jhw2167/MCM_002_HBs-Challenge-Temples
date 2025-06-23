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
