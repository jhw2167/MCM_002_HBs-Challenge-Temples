package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.externalapi.PortalApi;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.block.entity.SimpleBlockEntity;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.ServerTickEvent;
import com.holybuckets.foundation.model.ManagedChunkUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.holybuckets.challengetemple.ChallengeTempleMain.DEV_MODE;
import static com.holybuckets.challengetemple.ChallengeTempleMain.OVERWORLD_DIM;
import static com.holybuckets.challengetemple.core.TempleManager.*;

public class ManagedTemple {

    private static final String CLASS_ID = "006"; // Class ID for logging purposes

    private final BlockPos entityPos;
    private final BlockPos structurePos;
    private final BlockPos portalSourcePos;
    private final BlockPos overworldExitPos;
    private final long tickCreated;
    private SimpleBlockEntity templeEntity;

    private final Level level;
    private final String templeId;
    private ChallengeRoom challengeRoom;
    public Entity portalToChallenge;
    public Entity portalToHome;
    private boolean isCompleted;
    private volatile AtomicLong markForPortalCreation;

    Set<ServerPlayer> nearPlayers;  // players close enough to start challenge
    Set<ServerPlayer> activePlayers; //players taking challenge

    Thread watchChallengersThread;

    private static Vec3i STRUCTURE_OFFSET = new Vec3i(-4, -4, -2);

    private static final Vec3i SOURCE_OFFSET = new Vec3i(0, -1, 1);
    private static final Vec3i DEST_OFFSET = new Vec3i(4, 2, 1);
    private static final Vec3i RESPAWN_OFFSET = new Vec3i(0, 0, 1);
    private static final Vec3i SOURCE_EXIT_OFFSET = new Vec3i(0, 1, 3);
    private static final int CHALLENGE_DIM_HEIGHT = 64;

    /**
     * MUST RECORD AND PERSIST WHAT CHALLENGE WAS ATTACHED TO IT
     * @param level
     * @param pos
     */
    public ManagedTemple(Level level, BlockPos pos)
    {
        this.level = level;
        this.entityPos = pos;
        this.portalSourcePos = pos.offset(SOURCE_OFFSET);
        this.structurePos = pos.offset(STRUCTURE_OFFSET);
        this.overworldExitPos = pos.offset(SOURCE_EXIT_OFFSET);

        this.templeId = HBUtil.ChunkUtil.getId(pos);
        this.isCompleted = false;

        this.activePlayers = new HashSet<>();
        this.nearPlayers = new HashSet<>();

        this.markForPortalCreation = new AtomicLong(Long.MAX_VALUE);
        this.tickCreated = CONFIG.getTotalTickCount();
    }

    public void readEntityData()
    {
        if(this.templeEntity != null ) return;

        if(this.templeId.equals( SPECIAL_TEMPLE)) {
            int i = 0;
        }

        BlockEntity entity = level.getBlockEntity(this.entityPos);
        if( entity == null )
            return;

        if( !(entity instanceof SimpleBlockEntity) )
            return;

        this.templeEntity = (SimpleBlockEntity) entity;

        //if property "hasPortal"
        if(templeEntity.getProperty("hasPortals") != null) {
            if(templeEntity.getProperty("hasPortals").equals("true"))
                this.findPortals();
        }

        if( templeEntity.getProperty("complete")==null)
            this.isCompleted = false;
        else
            this.isCompleted = templeEntity.getProperty("complete").equals("true");

        String challengeId = templeEntity.getProperty("challengeId");
        if( (challengeId==null) || challengeId.isEmpty() ) return;
        this.buildChallenge(challengeId);
    }

        void findPortals()
        {
            AABB aabb = new AABB(
                this.portalSourcePos.getX() - 0.5, this.portalSourcePos.getY() - 0.5, this.portalSourcePos.getZ() - 0.5,
                this.portalSourcePos.getX() + 0.5, this.portalSourcePos.getY() + 0.5, this.portalSourcePos.getZ() + 0.5
            );
            List<Entity> entities = level.getEntities((Entity) null, aabb, PORTAL_API::isPortal);

            BlockPos destPos = this.getPortalDest();
            AABB destAABB = new AABB(
                destPos.getX() - 0.5, destPos.getY() - 0.5, destPos.getZ() - 0.5,
                destPos.getX() + 0.5, destPos.getY() + 0.5, destPos.getZ() + 0.5
            );
            List<Entity> destEntities = CHALLENGE_LEVEL.getEntities((Entity) null, destAABB, PORTAL_API::isPortal);
            if(entities.isEmpty() && destEntities.isEmpty() ) {
                if(this.isCompleted) return;
            }
            //Assign the last value in the list to portalToHome and portalToChallenge, delete the rest
            if (!entities.isEmpty()) {
                this.portalToChallenge = entities.get(entities.size() - 1);
                for (int i = 0; i < entities.size() - 1; i++) {
                    PORTAL_API.removePortal(entities.get(i));
                }
            }

            if (!destEntities.isEmpty()) {
                this.portalToHome = destEntities.get(destEntities.size() - 1);
                for (int i = 0; i < destEntities.size() - 1; i++) {
                    PORTAL_API.removePortal(destEntities.get(i));
                }
            }
        }

    public BlockPos getPortalSourcePos() {
        return this.portalSourcePos;
    }

    public BlockPos getStructurePos() {
        return structurePos;
    }

    public BlockPos getPortalDest() {
        return ChallengeRoom.getWorldPos(this.templeId).offset(DEST_OFFSET);
    }

    public BlockPos getChallengeRespawnPos() {
        return  this.getPortalDest().offset(RESPAWN_OFFSET);
    }

    public BlockPos getEntityPos() { return entityPos; }

    public Level getLevel() {
        return level;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getTempleId() {
        return templeId;
    }

    public ChallengeRoom getChallengeRoom() {
        return challengeRoom;
    }

    public boolean isMarkedForPortalCreation(long totalTicks) {
        return markForPortalCreation.get() < (totalTicks+1l);
    }

    public void setMarkedForPortalCreationTime() {
        long l = CONFIG.getTotalTickCount();
        markForPortalCreation.set(l+PORTAL_COOLDOWN);
    }

    //** CORE

    static final double P_HEIGHT = 2;
    static final double P_WIDTH = 2;

    public void createChallengePortal()
    {
        if( this.portalToChallenge != null )  return;
        deleteHomePortal();
        Vec3 sourcePos = HBUtil.BlockUtil.toVec3(this.getPortalSourcePos());
        Vec3 destination = HBUtil.BlockUtil.toVec3(this.getPortalDest());
        this.portalToChallenge = PORTAL_API.createPortal(P_WIDTH, P_HEIGHT, level,
            CHALLENGE_LEVEL, sourcePos, destination, Direction.NORTH);
        brickInOut(true);
    }

    public void deleteChallengePortal() {
        if (this.portalToChallenge != null)
        {
            PORTAL_API.removePortal(this.portalToChallenge);
            this.portalToChallenge = null;
        }
    }

    public void createHomePortal()
    {
        //deleteHomePortal();
        if(this.portalToHome != null) return;
        deleteChallengePortal();
        Vec3 sourcePos = HBUtil.BlockUtil.toVec3(this.getPortalSourcePos());
        Vec3 destination = HBUtil.BlockUtil.toVec3(this.getPortalDest());
        this.portalToHome = PORTAL_API.createPortal(P_WIDTH, P_HEIGHT, CHALLENGE_LEVEL,
            level, destination, sourcePos, Direction.SOUTH);
        brickInOut(false);
    }

    public void deleteHomePortal() {
        if (this.portalToHome != null) {
            PORTAL_API.removePortal(this.portalToHome);
            this.portalToHome = null;
        }
    }

        private static final List<Vec3i> BRICK_OFFSETS = List.of(
        new Vec3i(-1,0, -1)
        , new Vec3i(0,0, -1)
        , new Vec3i(-1,-1, -1)
        , new Vec3i(0,-1, -1)
        );

        private void brickInOut(boolean brickIn) {
            BlockPos start = this.getPortalDest();
            BlockState b = (brickIn) ? ModBlocks.challengeBrick.defaultBlockState()
            : Blocks.AIR.defaultBlockState();
            for( Vec3i offset : BRICK_OFFSETS) {
                CHALLENGE_LEVEL.setBlock( start.offset(offset), b, 18);
            }
        }

    public void buildChallenge(String challengeId)
    {

        if(templeId.equals( SPECIAL_TEMPLE)) {
            int i = 0;
        }

        if( DEV_MODE ) this.isCompleted = true;
        if(this.isCompleted) return;

        if(this.activePlayers.isEmpty())
            createChallengePortal();
        else
            createHomePortal();

        if (this.challengeRoom == null)
        {
            this.challengeRoom = new ChallengeRoom( this.templeId, overworldExitPos,
                 this.level, challengeId);

            if(this.activePlayers.isEmpty())
                this.challengeRoom.loadStructure(); // don't reload if player is inside

            this.templeEntity.setProperty("challengeId", this.challengeRoom.getChallengeId());
        }

    }

    public void swapPortals()
    {
        this.findPortals();
        this.deleteChallengePortal();
        this.deleteHomePortal();

        if( this.activePlayers.isEmpty() ) {
            this.createChallengePortal();
            this.markForPortalCreation.set(Long.MAX_VALUE);
        } else  {
            this.createHomePortal();
            this.markForPortalCreation.set(Long.MAX_VALUE);
        }

    }

    static final long PORTAL_COOLDOWN = 100l;
    public void playerTakeChallenge(ManagedChallenger player)
    {
        if( player == null || player.getServerPlayer() == null) {
            return;
        }

        //this.deleteHomePortal();
        //this.deleteChallengePortal();

        this.setMarkedForPortalCreationTime();

        if (activePlayers.contains(player.getServerPlayer())) return;
        player.startChallenge(this);
        brickInOut(true);

        if( activePlayers.size() == 0)
            this.challengeRoom.startChallenge();
        activePlayers.add(player.getServerPlayer());
    }

    public void challengeComplete(ServerPlayer p)
    {
        this.deleteHomePortal();
        this.deleteChallengePortal();

        this.isCompleted = true;
        this.markForPortalCreation.set(Long.MAX_VALUE);
    }

    public void playerJoinedInChallenge(ServerPlayer p) {
        nearPlayers.add(p);
        activePlayers.add(p);
        this.challengeRoom.setActive( true );
    }

    public void playerDiedInChallenge(ManagedChallenger c) {
        challengeRoom.onChallengerDeath(c);
        if( challengeRoom.isChallengerFailed(c) )
            this.kickChallenger(c);
    }
        private void kickChallenger(ManagedChallenger c) {

        }

    public void playerEndChallenge(ManagedChallenger player)
    {
        boolean containedPlayer = activePlayers.remove(player.getServerPlayer());
        if(!containedPlayer) return;
        if( activePlayers.isEmpty() ) {
            this.challengeRoom.setActive( false );
            this.deleteHomePortal();
        }

        //this.deleteHomePortal();
        //this.deleteChallengePortal();

        if(challengeRoom.isRoomCompleted()) {
            this.challengeComplete(player.getServerPlayer());
            player.completedChallenge(this);
            loadRewardsChest();
        } else {
           this.setMarkedForPortalCreationTime();
        }


        player.endChallenge(this);
        this.challengeRoom.removeGravePos(player.lastGravePos);

        /*
        if (watchChallengersThread == null || !watchChallengersThread.isAlive()) {
            startWatchChallengers();
        }
        */
    }

    private static Vec3i REWARDS_CHEST_OFFSET = new Vec3i(0, -1, 4);
    public void loadRewardsChest()
    {
        BlockPos chestPos = this.entityPos.offset(REWARDS_CHEST_OFFSET);
        BlockEntity chestEntity = level.getBlockEntity(chestPos);
        if (chestEntity instanceof ChestBlockEntity) {
            ChestBlockEntity chest = (ChestBlockEntity) chestEntity;
            int i = 0;
            for( ItemStack stack : challengeRoom.getChallengeLoot() ) {
                chest.setItem(i++, stack);
            }

        } else {
            Vec3 pos = toVec3(chestPos).add(0.5, 0.5, 0.5);
            for( ItemStack stack: challengeRoom.getChallengeLoot() ) {
                ItemEntity ent = new ItemEntity(level, pos.x, pos.y, pos.z, stack);
                level.addFreshEntity(ent);
            }
        }

    }


    //** UTILITY
    private static Vec3 toVec3(BlockPos pos) {
        return HBUtil.BlockUtil.toVec3(pos);
    }

    public boolean isFullyLoaded() {
        return ManagedChunkUtility.isChunkFullyLoaded(level, this.templeId);
    }

    public boolean hasPortal() {
        return (this.isCompleted ||  this.challengeRoom != null);
    }


    public boolean playerInPortalRange()
    {
        HBUtil.TripleInt source = new HBUtil.TripleInt(this.entityPos);
        boolean isClose = this.level.hasNearbyAlivePlayer(source.x, source.y, source.z, 128);
        return isClose;
    }

    public boolean playerInChallenge(ManagedChallenger c) {
        if (c == null || c.getServerPlayer() == null) return false;
        return activePlayers.contains(c.getServerPlayer());
    }

    //** EVENTS
    public void onPlayerLeave(ServerPlayer p) {
        if (p == null) return;
        this.activePlayers.remove(p);
    }


    void shutdown()
    {
        if (watchChallengersThread != null && watchChallengersThread.isAlive()) {
            watchChallengersThread.interrupt();
        }

        if (challengeRoom != null) {
            challengeRoom.roomShutdown();
        }

        this.findPortals();
        this.deleteHomePortal();
        this.deleteChallengePortal();

        this.nearPlayers.clear();
        this.activePlayers.clear();
    }


    //** Statics
    static void init(EventRegistrar reg) {
        reg.registerOnServerTick(EventRegistrar.TickType.ON_20_TICKS, ManagedTemple::onServer20Ticks);
    }

    static void onServer20Ticks(ServerTickEvent event) {
        Level overworld = HBUtil.LevelUtil.toLevel(HBUtil.LevelUtil.LevelNameSpace.SERVER, OVERWORLD_DIM);
        for (ManagedTemple temple : MANAGERS.get(overworld).getTemples() ) {
            if (temple.isFullyLoaded()) {
                temple.onTick();
            }
        }
    }

        void onTick() {
            int i = 0;
        }


    public void playerQuitChallenge(ManagedChallenger managedChallenger) {
        //Teleport the serverPlayer to the templeDestPos
        ServerPlayer sp = managedChallenger.getServerPlayer();
        if (sp == null) return;
        BlockPos p = this.getPortalDest();
        sp.teleportTo(p.getX() + 0.5, p.getY() - 0.5, p.getZ() + 0.5);
        this.challengeRoom.refreshStructure();
    }
}
