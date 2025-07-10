package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.ChallengeTempleMain;
import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.challengetemple.externalapi.PortalApi;
import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.block.entity.SimpleBlockEntity;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.ServerTickEvent;
import net.blay09.mods.balm.api.event.ChunkLoadingEvent;
import net.blay09.mods.balm.api.event.PlayerChangedDimensionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

import static com.holybuckets.foundation.HBUtil.LevelUtil;

public class TempleManager {


    public static String CLASS_ID = "005";

    static ServerLevel CHALLENGE_LEVEL;
    static Map<LevelAccessor, TempleManager> MANAGERS;
    static PortalApi PORTAL_API;

    private final ServerLevel level;

    private final GeneralConfig generalConfig;

    private final Map<String, ManagedTemple> temples;

    public TempleManager(ServerLevel level)
    {
        this.level = level;
        this.generalConfig = GeneralConfig.getInstance();

        this.temples = new HashMap<>();
        MANAGERS.put(level, this);
        this.load();
    }

    public static void init(EventRegistrar reg) {
        reg.registerOnChunkLoad(TempleManager::onChunkLoad);
        reg.registerOnChunkUnload(TempleManager::onChunkUnload);
        reg.registerOnServerTick(EventRegistrar.TickType.ON_120_TICKS, TempleManager::onServerTick120 );

        //ManagedTemple.init();
        ChallengeRoom.init(reg);

        MANAGERS = new HashMap<>();

        PORTAL_API = ChallengeTempleMain.INSTANCE.portalApi;
    }

    public void load() {
        LoggerProject.logInfo("005000", "Loading TempleManager for level: " + this.level);
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

    public ServerLevel getChallengeLevel() {
        return this.CHALLENGE_LEVEL;
    }

    public static void setChallengeLevel(ServerLevel challengeLevel) {
        CHALLENGE_LEVEL = challengeLevel;
        ChallengeRoom.load();
    }

    //** CORE
    private void workerThreadReadEntityData()
    {
        temples.values().stream()
            .filter(t -> t.isFullyLoaded() )
            .forEach(ManagedTemple::readEntityData);
    }

    private void workerThreadBuildPortal()
    {
        temples.values().stream()
            .filter(t -> t.isFullyLoaded() )
            .filter(t -> !t.hasPortal() )
            .filter(t -> t.playerInPortalRange() )
            .forEach( this::handleBuildPortal );

        //activate startWatchChallengers for all temples with portals
        temples.values().stream()
            .filter(ManagedTemple::hasPortal)
            .forEach(ManagedTemple::startWatchChallengers);
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
    static final double P_HEIGHT = 2;
    static final double P_WIDTH = 2;

    private static boolean DISABLE_PORTALS = false;

    private void handleBuildPortal(ManagedTemple temple)
    {
        if( DISABLE_PORTALS ) return;

        BlockPos pos = temple.getEntityPos();
        if( HBUtil.ChunkUtil.getId(pos).equals("0,0") ) {
            System.out.println("no portal at 0,0: " );
            return;
        }

        if( temple.hasPortal() ) {
            LoggerProject.logDebug("005010", "Temple already has a portal: " + temple.getTempleId());
            return;
        }

        Vec3 sourcePos = HBUtil.BlockUtil.toVec3(temple.getPortalSourcePos());
        Vec3 destination = HBUtil.BlockUtil.toVec3( temple.getPortalDest() );
        temple.portalToChallenge = PORTAL_API.createPortal(P_WIDTH, P_HEIGHT, level,
             CHALLENGE_LEVEL, sourcePos, destination, PortalApi.Direction.SOUTH);

        temple.portalToHome = PORTAL_API.createPortal(P_WIDTH, P_HEIGHT, CHALLENGE_LEVEL,
            level, destination, sourcePos, PortalApi.Direction.NORTH);

        temple.buildChallenge();
    }

    public static ManagedTemple handlePlayerJoinedInTemple( ServerPlayer p, Level templeLevel, String id )
    {
        TempleManager m = MANAGERS.get(templeLevel);
        if( m != null ) {
            m.temples.get(id).playerJoinedInChallenge(p);
            return m.temples.get(id);
        }
        return null;
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
        workerThreadReadEntityData();
        workerThreadBuildPortal();
    }


    //* EVENTS
    public static void onPlayerChangeDimension(PlayerChangedDimensionEvent event, ManagedChallenger c)
    {
        Level dimFrom = LevelUtil.toLevel(LevelUtil.LevelNameSpace.SERVER, event.getFromDim() );
        Level dimTo = LevelUtil.toLevel(LevelUtil.LevelNameSpace.SERVER, event.getToDim() );
        LoggerProject.logDebug("005010", "Player changed dimension from " + dimFrom + " to " + dimTo);

        String chunkId = HBUtil.ChunkUtil.getId(event.getPlayer().getOnPos());
        if( dimFrom == CHALLENGE_LEVEL ) {
            ManagedTemple temple = c.getActiveTemple();
            if(temple != null) temple.playerEndChallenge(c);
        } else if( dimTo == CHALLENGE_LEVEL ) {
            MANAGERS.get(dimFrom).temples.get(chunkId).playerTakeChallenge(c);
        }

    }


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



    public void shutdown() {
        LoggerProject.logInfo("005999", "Shutting down TempleManager for level: " + this.level);
        temples.values().forEach(ManagedTemple::shutdown);
        temples.clear();

        ChallengeRoom.shutdown();
        MANAGERS.remove(this.level);
    }


}
