package com.holybuckets.challengetemple.externalapi;

import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.foundation.GeneralConfig;
import eu.pb4.graves.GravesApi;
import eu.pb4.graves.GravesMod;
import eu.pb4.graves.grave.Grave;
import eu.pb4.graves.grave.GraveManager;
import eu.pb4.graves.grave.GraveType;
import eu.pb4.graves.grave.PositionedItemStack;
import eu.pb4.graves.mixin.PlayerEntityAccessor;

import eu.pb4.graves.registry.GraveBlock;
import eu.pb4.graves.registry.GraveBlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;


import java.util.*;
import java.util.stream.Collectors;

/**
 * API for managing player inventory snapshots using UniversalGraves functionality
 */
public class FabricInventoryApi implements InventoryApi {

    private static FabricInventoryApi INSTANCE;

    private final Map<ServerPlayer, GraveBlockEntity> playerGraves = new HashMap<>();
    private final List<GraveBlockEntity> allGraves = new LinkedList<>();
    private static ServerLevel CHALLENGE_LEVEL;

    @Override
    public void setChallengeLevel(ServerLevel level) {
        CHALLENGE_LEVEL = level;
    }

    @Override
    public void setInstance(InventoryApi ai) {
        INSTANCE = (FabricInventoryApi) ai;
    }
    public static FabricInventoryApi getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a grave at the specified location containing the player's current inventory
     *
     * @param player   The player whose inventory to store in the grave
     * @param position The position where the grave should be created
     * @return true if grave creation was successful
     */
    @Override
    public boolean createGrave(ServerPlayer player, BlockPos position) {
        //We also want to hijack universalGraves.GraveUtils.createGrave to intercept it during temple stuff
        try {
            // Collect items from player inventory
            List<PositionedItemStack> items = new ArrayList<>();

            for (var mask : GravesApi.getAllInventoryMasks()) {
                try {
                    mask.addToGrave(player, (stack, slot, nbt, tags) -> items.add(new PositionedItemStack(stack, slot, mask, nbt, Set.of(tags))));
                } catch (Throwable e) {
                    GravesMod.LOGGER.error("Failed to add items from '{}'!", mask.getId(), e);
                }
            }

            // Create new grave with collected items
            ServerLevel level = CHALLENGE_LEVEL;
            Grave grave = new Grave(
                GraveManager.INSTANCE.requestId(),
                player.getGameProfile(),
                player.getEntityData().get(PlayerEntityAccessor.getPLAYER_MODEL_PARTS()), // verify accessor
                player.getMainArm(),
                position,
                level.dimension().location(),   // was getRegistryKey().getValue()
                GraveType.BLOCK,
                System.currentTimeMillis() / 1000,
                GraveManager.INSTANCE.getCurrentGameTime(),
                player.experienceLevel,
                Component.literal("Manual grave creation"),  // was Text.literal
                Collections.singleton(player.getUUID()),     // was getUuid()
                items,
                true,
                (int) (level.getGameTime() / 24000L)  // was player.getWorld().getTime()
            );


            // Add grave to manager and clear player inventory
            //create anonymous runnable with above code
            GravesMod.DO_ON_NEXT_TICK.add(() -> {
                level.setBlock(position, GraveBlock.INSTANCE.defaultBlockState(), 3);
                BlockEntity entity = level.getBlockEntity(position);
                if (entity instanceof GraveBlockEntity graveBlockEntity) {
                    GraveManager.INSTANCE.add(grave);
                    graveBlockEntity.setGrave(grave, GraveBlock.INSTANCE.defaultBlockState());
                    entity.setChanged();
                    playerGraves.put(player, graveBlockEntity);
                    allGraves.add(graveBlockEntity);
                }
                GravesMod.LOGGER.info("Grave created for player {} at {}", player.getName().getString(), position);
            });
            //clearInventory(player);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Restores a player's inventory from their last recorded grave entity
     *
     * @param player   The player whose inventory to restore
     * @param gravePos The position of the grave block entity,
     *                 if the server was shutdown recently and we must pull data from the world
     * @return true if restoration was successful, false otherwise
     */
    @Override
    public boolean returnInventory(ServerPlayer player, BlockPos gravePos) {
        GraveBlockEntity graveEntity = playerGraves.get(player);
        if (graveEntity == null) {
            if( gravePos == null ) return false;
            graveEntity = (GraveBlockEntity) CHALLENGE_LEVEL.getBlockEntity(gravePos);
        }
        if (graveEntity == null || graveEntity.getGrave() == null)
            return false;

        // Clear current inventory first
        //player.getInventory().clear();

        // Restore items using VanillaInventoryMask
        graveEntity.getGrave().quickEquip(player);
        graveEntity.getGrave().destroyGrave(GeneralConfig.getInstance().getServer(), null);
        graveEntity.breakBlock();
        LoggerProject.logDebug("000111", "Returned inventory");
        return true;
    }

    /**
     * Clean up our graves occaisionally so we don't get overwhelemed
     *
     * @param p      The player who is trying to destroy their grave
     * @param server
     * @return true if the operation was successful, false if there was nothing to destroy
     */
    public boolean destroyGrave(BlockPos p, MinecraftServer server) {
        return false;
    }

    /**
     * Iterates over all graves and clears any graves NOT in playerGraves map
     * call it periodically or something
     *
     * @param server
     */
    @Override
    public void clearUnusedGraves(MinecraftServer server)
    {
        /*
        Set<GraveBlockEntity> existingGraves = playerGraves.values().stream().collect(Collectors.toSet());
        allGraves.removeIf(grave -> {
            if (!existingGraves.contains(grave)) {
                grave.getGrave().destroyGrave(server, null);
                grave.breakBlock();
                return true;
            }
            return false;
        });
        */

    }

    @Override
    public Map<ServerPlayer, BlockPos> getGravePos() {
        return playerGraves.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getBlockPos()));
    }

    @Override
    public void setProtectedGravePos(List<BlockPos> positions) {

    }

    //Utility method to treate graves I am using as distinct from other graves in the mod
    public boolean hasGrave(Grave grave) {
        return allGraves.stream()
                .anyMatch(graveBlockEntity -> graveBlockEntity.getGrave().equals(grave));
    }

}