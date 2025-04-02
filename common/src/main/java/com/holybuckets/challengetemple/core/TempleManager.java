package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.portal.PortalApi;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import net.blay09.mods.balm.api.event.ChunkLoadingEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class TempleManager {

    public static String CLASS_ID = "005";

    private static Map<LevelAccessor, TempleManager> MANAGERS;
    private final Map<String, ManagedTemple> temples;

    private final ServerLevel level;
    private final PortalApi portalApi;

    public TempleManager(ServerLevel level, PortalApi portalApi)
    {
        this.level = level;
        this.portalApi = portalApi;

        this.temples = new HashMap<>();
        MANAGERS.put(level, this);
    }

    public static void init(EventRegistrar reg) {
        reg.registerOnChunkLoad(TempleManager::onChunkLoad);
        reg.registerOnChunkUnload(TempleManager::onChunkUnload);
        MANAGERS = new HashMap<>();
    }

    //** UTILITY
    public ManagedTemple registerTemple(Level level, BlockPos pos) {
        ManagedTemple temple = new ManagedTemple(level, pos);
        temples.putIfAbsent(temple.getTempleId(), temple);
        return temple;
    }

    public void removeTemple(String id) {
        temples.remove(id);
    }

    public ManagedTemple getTemple(String id) {
        return temples.get(id);
    }

    public void clear() {
        temples.clear();
    }

    public static TempleManager get(Level level) {
        return MANAGERS.get(level);
    }


    /**
     * Description: 1. Finds all temples in the world
     * 2. Checks if the temple is in a loaded chunk
     * 3. If so, builds portal
     *
     */
    private void workerThreadBuildPortal()
    {
        temples.values().stream()
            .filter(t -> t.isFullyLoaded() )
            .filter(t -> !t.hasPortal() )
            .forEach( this::handleBuildPortal );
    }

    //* HANDLERS

    /**
     * Finds and registers a temple structure in the world
      * @param c
     */
    private void handleFindTemple(ChunkAccess c)
    {
        /*
        BlockPos structurePos = level.findNearestMapStructure(
            StructureTags.CHALLENGE_STRUCTURE,
            c.getPos().getWorldPosition(),
            100,
            false
        );
        */
        if( !TempleUtility.candidateChunkForTemple(c) ) return;

        BlockPos structurePos = TempleUtility.findTempleBlockEntity(c);

        if(structurePos != null) {
            registerTemple(level, structurePos);
        }

    }

    /**
     * Creates a portal using the portal API
     * @param temple
     */
    private static final double P_HEIGHT = 2;
    private static final double P_WIDTH = 2;
    private static final String DIM_ID = Constants.MOD_ID + ":challenge_dimension";

    private static final Vec3i OFFSET = new Vec3i(0, 0, 0);
    private static final int CHALLENGE_DIM_HEIGHT = 70;

    private void handleBuildPortal(ManagedTemple temple)
    {
        BlockPos pos = temple.getPortalSourcePos().offset(OFFSET);
        Entity spawnEntity = EntityType.ARMOR_STAND.create(
            this.level,
            null,               // Optional NBT data (e.g., from a spawn egg or saved entity)
            null,                   // Optional function to customize the entity after creation
            pos,                 // The target block position for spawning the entity
            MobSpawnType.COMMAND,            // The reason/type of spawn (e.g., NATURAL, SPAWNER, COMMAND)
            true,               // Whether to position the entity above the block center (usually true)
            true              // Whether to adjust the entity’s Y offset based on the terrain
        );

        LevelAccessor dim = HBUtil.LevelUtil.toLevel( HBUtil.LevelUtil.LevelNameSpace.SERVER, DIM_ID );
        Vec3 destination = new Vec3(pos.getX(), CHALLENGE_DIM_HEIGHT, pos.getZ());
        //temple.portal = portalApi.createPortal(P_WIDTH, P_HEIGHT, spawnEntity, dim, destination);

    }




    //* EVENTS
    public static void onChunkLoad(ChunkLoadingEvent.Load event) {
        TempleManager m = MANAGERS.get(event.getLevel());
        if (m != null) {
            m.handleChunkLoaded(event.getChunk());
        }
    }

    public static void onChunkUnload(ChunkLoadingEvent.Unload event) {
        TempleManager m = MANAGERS.get(event.getLevel());
        if (m != null) {
            m.handleChunkUnloaded(event.getChunk());
        }
    }

    private void handleChunkLoaded(ChunkAccess c) {
        //LoggerProject.logInfo( "00500","Chunk loaded: " + c.getPos());
        handleFindTemple(c);
        workerThreadBuildPortal();
    }

    private void handleChunkUnloaded(ChunkAccess c) {
        this.temples.remove(HBUtil.ChunkUtil.getId(c.getPos()));
        //LoggerProject.logInfo( "00501","Chunk unloaded: " + c.getPos());
    }
}
