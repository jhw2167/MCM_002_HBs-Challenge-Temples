package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.portal.PortalApi;
import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.block.entity.SimpleBlockEntity;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.ServerTickEvent;
import net.blay09.mods.balm.api.event.ChunkLoadingEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class TempleManager {

    public static String CLASS_ID = "005";

    private static Map<LevelAccessor, TempleManager> MANAGERS;

    private final ServerLevel level;
    private ServerLevel challengeLevel;
    private final PortalApi portalApi;
    private final GeneralConfig generalConfig;

    private final Map<String, ManagedTemple> temples;

    public TempleManager(ServerLevel level, PortalApi portalApi)
    {
        this.level = level;
        this.portalApi = portalApi;
        this.generalConfig = GeneralConfig.getInstance();

        this.temples = new HashMap<>();
        MANAGERS.put(level, this);
    }

    public static void init(EventRegistrar reg) {
        reg.registerOnChunkLoad(TempleManager::onChunkLoad);
        reg.registerOnChunkUnload(TempleManager::onChunkUnload);
        reg.registerOnServerTick(EventRegistrar.TickType.ON_120_TICKS, TempleManager::onServerTick120 );
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

    public void setChallengeLevel(ServerLevel challengeLevel) {
        this.challengeLevel = challengeLevel;
        ChallengeRoom.CHALLENGE_LEVEL = challengeLevel;
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
            .filter(t -> t.hasNearPlayer() )
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

    private static boolean DISABLE_PORTALS = false;

    private void handleBuildPortal(ManagedTemple temple)
    {
        if( DISABLE_PORTALS ) return;

        BlockPos pos = temple.getEntityPos();
        if( HBUtil.ChunkUtil.getId(pos).equals("0,0") ) {
            System.out.println("no portal at 0,0: " );
            return;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if( entity == null ) return;
        if( entity instanceof SimpleBlockEntity ) {
            SimpleBlockEntity be = (SimpleBlockEntity) entity;
            if( be.getProperty("hasPortal") == null ) {

            } else if (be.getProperty("hasPortal").equals("true")) {
                return;
            }
            be.setProperty("hasPortal", "true");
        }

        Vec3 sourcePos = toVec3(temple.getPortalSourcePos());
        Vec3 destination = toVec3( temple.getPortalDest() );
        temple.portalToChallenge = portalApi.createPortal(P_WIDTH, P_HEIGHT, level,
             this.challengeLevel, sourcePos, destination, PortalApi.Direction.SOUTH);

        temple.portalToHome = portalApi.createPortal(P_WIDTH, P_HEIGHT, this.challengeLevel,
            level, destination, sourcePos, PortalApi.Direction.NORTH);

        temple.buildChallenge();

    }

    //BlockUtil
    public Vec3 toVec3(BlockPos pos) {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    private void handleChunkLoaded(ChunkAccess c) {
        //LoggerProject.logInfo( "00500","Chunk loaded: " + c.getPos());
        handleFindTemple(c);
    }

    private void handleChunkUnloaded(ChunkAccess c) {
        this.temples.remove(HBUtil.ChunkUtil.getId(c.getPos()));
        //LoggerProject.logInfo( "00501","Chunk unloaded: " + c.getPos());
    }

    private void handleOnServerTicks120() {
        workerThreadBuildPortal();
    }


    public void shutdown() {
        //nothing
    }


    //* EVENTS
    private static void onChunkLoad(ChunkLoadingEvent.Load event) {
        TempleManager m = MANAGERS.get(event.getLevel());
        if (m != null) {
            m.handleChunkLoaded(event.getChunk());
        }
    }

    private static void onChunkUnload(ChunkLoadingEvent.Unload event) {
        TempleManager m = MANAGERS.get(event.getLevel());
        if (m != null) {
            m.handleChunkUnloaded(event.getChunk());
        }
    }

    private static void onServerTick120(ServerTickEvent e) {
        MANAGERS.values().forEach(TempleManager::handleOnServerTicks120);
    }


    public ServerLevel getChallengeLevel() {
        return this.challengeLevel;
    }
}
