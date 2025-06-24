package com.holybuckets.challengetemple.externalapi;

import com.holybuckets.foundation.HBUtil;
import de.maxhenkel.corpse.corelib.death.Death;
import de.maxhenkel.corpse.entities.CorpseEntity;
import de.maxhenkel.corpse.gui.CorpseInventoryContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.stream.Collectors;

// Simple InventoryApi interface (you must define in your common code)
public class ForgeInventoryApi implements InventoryApi {

    private static ForgeInventoryApi INSTANCE;
    private final Map<ServerPlayer, CorpseEntity> playerGraves = new HashMap<>();
    private final List<BlockPos> protectedGraves = new ArrayList<>();
    private final List<CorpseEntity> allGraves = new LinkedList<>();

    private static Level CHALLENGE_LEVEL;
    @Override
    public void setChallengeLevel(ServerLevel level) {
        CHALLENGE_LEVEL = level;
    }

    @Override
    public void setInstance(InventoryApi api) {
        INSTANCE = (ForgeInventoryApi) api;
    }

    public static ForgeInventoryApi getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean createGrave(ServerPlayer player, BlockPos position)
    {
        //Player death deatails
        Death death = Death.fromPlayer(player);
        CorpseEntity corpse = CorpseEntity.createFromDeath(player, death);
        corpse.setPos(HBUtil.BlockUtil.toVec3(position));

        // Add corpse to world
        CHALLENGE_LEVEL.addFreshEntity(corpse);

        //track corpse
        playerGraves.put(player, corpse);
        allGraves.add(corpse);

        return true;
    }

    public Map<ServerPlayer, BlockPos> getPlayerGraves() {
        Map<ServerPlayer, BlockPos> graves = new HashMap<>();
        for (Map.Entry<ServerPlayer, CorpseEntity> entry : playerGraves.entrySet()) {
            ServerPlayer player = entry.getKey();
            CorpseEntity grave = entry.getValue();
            if (grave != null) {
                graves.put(player, grave.blockPosition());
            }
        }
        return graves;
    }

    public List<BlockPos> getProtectedGraves() {
        return protectedGraves;
    }

    @Override
    public boolean returnInventory(ServerPlayer player, BlockPos gravePos) {
        try {
            // TODO: Implement inventory restoration using Corpse mod
            CorpseEntity grave = playerGraves.get(player);
            if (grave == null)
            {
                //within 1 block of gravePos
                AABB graveArea = new AABB(
                    gravePos.getX() - 1, gravePos.getY() - 1, gravePos.getZ() - 1,
                    gravePos.getX() + 1, gravePos.getY() + 1, gravePos.getZ() + 1
                );
                List<Entity> entities = CHALLENGE_LEVEL.getEntities(null, graveArea);
                if( !entities.isEmpty() && (entities.get(0) instanceof CorpseEntity) ) {
                    grave = (CorpseEntity) entities.get(0);
                } else {
                    return false; // No grave found
                }
            }
            CorpseInventoryContainer container = new CorpseInventoryContainer(
            HBUtil.BlockUtil.mapTo1DNumber(grave.blockPosition()),
            player.getInventory(), grave, true, false);
            container.transferItems();
            grave.getEquipment().clear();
            grave.discard();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void clearUnusedGraves(MinecraftServer server) {
        // Remove graves that aren't in the protected list
        allGraves.removeIf(corpse -> {
            BlockPos pos = corpse.blockPosition();
            boolean isProtected = protectedGraves.stream().anyMatch(p -> p.equals(pos));
            if (!isProtected) {
                corpse.discard(); // Remove the corpse entity
                return true; // Remove from allGraves list
            }
            return false; // Keep the grave
        });
    }

    @Override
    public Map<ServerPlayer, BlockPos> getGravePos() {
        return playerGraves.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().blockPosition()));
    }

    @Override
    public void setProtectedGravePos(List<BlockPos> positions) {
        this.protectedGraves.addAll(positions);
    }
}
