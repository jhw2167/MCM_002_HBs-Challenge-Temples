package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.externalapi.PortalApi;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.block.entity.SimpleBlockEntity;
import com.holybuckets.foundation.model.ManagedChunkUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.holybuckets.challengetemple.core.TempleManager.*;

public class ManagedTemple {

    private static final String CLASS_ID = "006"; // Class ID for logging purposes

    private final BlockPos entityPos;
    private final BlockPos structurePos;
    private final BlockPos portalSourcePos;
    private final BlockPos overworldExitPos;
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
    private static final Vec3i RESPAWN_OFFSET = new Vec3i(0, -1, 1);
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
    }

    public void readEntityData()
    {
        if(this.templeEntity != null ) return;

        BlockEntity entity = level.getBlockEntity(this.entityPos);
        if( entity == null )
            return;

        if( !(entity instanceof SimpleBlockEntity) )
            return;

        this.templeEntity = (SimpleBlockEntity) entity;
        if( templeEntity.getProperty("hasPortal") == null )
            return;

        if( templeEntity.getProperty("complete")==null)
            this.isCompleted = false;
        else
            this.isCompleted = templeEntity.getProperty("complete").equals("true");

        if( templeEntity.getProperty("hasPortal").equals("true") )
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
            if(!entities.isEmpty() && !destEntities.isEmpty() ) {
                this.portalToChallenge = entities.get(0);
                this.portalToHome = destEntities.get(0);
            } else if(!entities.isEmpty()) {
                entities.get(0).discard();
            } else if(!destEntities.isEmpty()) {
                destEntities.get(0).discard();
            }

            this.markForPortalCreation.set(0l);

            String challengeId = templeEntity.getProperty("challengeId");
            if( (challengeId==null) || challengeId.isEmpty() ) return;

            this.challengeRoom = new ChallengeRoom(templeId,
                this.overworldExitPos,
                this.level,
                challengeId);
            this.templeEntity.setProperty("challengeId", this.challengeRoom.getChallengeId());
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

    //** CORE

    public void startWatchChallengers()
    {
        if( watchChallengersThread != null ) // Stop the previous thread if it's still running
            return;

        //We need to save this thread in a variable so we can interrupt it on shutdown
        this.watchChallengersThread = new Thread( this::threadWatchChallengers);
        this.watchChallengersThread.start();
    }

    //Add ServerPlayer to hashSet if he is 16u away from block entity pos
    //keep while(1) loop, surround with try catch for interrupt, wait 100ms between loops
    void threadWatchChallengers()
    {
        try {
            while (true)
            {
                Vec3i p = entityPos;
                nearPlayers.clear();
                List<? extends Player> players = this.level.getNearbyPlayers(
                    TargetingConditions.forNonCombat().range(16),
                    null,
                    new AABB(
                        p.getX() - 8, p.getY() - 8, p.getZ() - 8,
                        p.getX() + 8, p.getY() + 8, p.getZ() + 8
                    )
                );
                players.stream()
                    .filter(p1 -> p1 instanceof ServerPlayer)
                    .map(p1 -> (ServerPlayer) p1)
                    .forEach(nearPlayers::add);
                Thread.sleep(100); // Sleep for 100 milliseconds
            }
        } catch (InterruptedException e) {
            // Thread was interrupted, exit the loop
    }


    }

    static final double P_HEIGHT = 2;
    static final double P_WIDTH = 2;

    public void createChallengePortal()
    {
        deleteChallengePortal();
        Vec3 sourcePos = HBUtil.BlockUtil.toVec3(this.getPortalSourcePos());
        Vec3 destination = HBUtil.BlockUtil.toVec3(this.getPortalDest());
        this.portalToChallenge = PORTAL_API.createPortal(P_WIDTH, P_HEIGHT, level,
            CHALLENGE_LEVEL, sourcePos, destination, PortalApi.Direction.SOUTH);
    }

    public void deleteChallengePortal() {
        if (this.portalToChallenge != null) {
            this.portalToChallenge.discard();
            this.portalToChallenge = null;
        }
    }

    public void createHomePortal()
    {
        deleteHomePortal();
        Vec3 sourcePos = HBUtil.BlockUtil.toVec3(this.getPortalSourcePos());
        Vec3 destination = HBUtil.BlockUtil.toVec3(this.getPortalDest());
        this.portalToHome = PORTAL_API.createPortal(P_WIDTH, P_HEIGHT, CHALLENGE_LEVEL,
            level, destination, sourcePos, PortalApi.Direction.NORTH);
    }

    public void deleteHomePortal() {
        if (this.portalToHome != null) {
            this.portalToHome.discard();
            this.portalToHome = null;
        }
    }

    public void buildChallenge()
    {
        createChallengePortal();
        createHomePortal();

        this.templeEntity.setProperty("hasPortal", "true");
        if (this.challengeRoom == null) {
            this.challengeRoom = new ChallengeRoom( this.templeId, overworldExitPos,
                 this.level);
            this.templeEntity.setProperty("challengeId", this.challengeRoom.getChallengeId());
        }
        

        this.startWatchChallengers();
    }

    static final long PORTAL_COOLDOWN = 100l;
    public void playerTakeChallenge(ManagedChallenger player)
    {
        if( player == null || player.getServerPlayer() == null) {
            return;
        }

        this.deleteHomePortal();
        this.deleteChallengePortal();
        long currentTicks = CONFIG.getTotalTickCount();
        this.markForPortalCreation.set(currentTicks + PORTAL_COOLDOWN);

        if( nearPlayers.contains(player.getServerPlayer()) ) {
            if (activePlayers.contains(player.getServerPlayer())) return;
            activePlayers.add(player.getServerPlayer());
            player.startChallenge(this);
        }

        this.challengeRoom.startChallenge();
    }

    public void challengeComplete(ServerPlayer p) {
        this.isCompleted = true;
        if(this.templeEntity != null)
            this.templeEntity.setProperty("hasPortal", "true");
        this.markForPortalCreation.set(Long.MAX_VALUE);
    }

    public void playerJoinedInChallenge(ServerPlayer p) {
        nearPlayers.add(p);
        activePlayers.add(p);
        this.challengeRoom.setActive( true );
    }

    private static Vec3i REWARDS_CHEST_OFFSET = new Vec3i(0, -1, 4);
    public void playerEndChallenge(ManagedChallenger player)
    {
        boolean containedPlayer = activePlayers.remove(player.getServerPlayer());
        if(!containedPlayer) return;
        if( activePlayers.isEmpty() ) {
            this.challengeRoom.setActive( false );
        }

        this.deleteHomePortal();
        this.deleteChallengePortal();

        if(challengeRoom.isRoomCompleted()) {
            this.challengeComplete(player.getServerPlayer());
            player.completedChallenge(this);
            loadRewardsChest();
        } else {
            long currentTicks = CONFIG.getTotalTickCount();
            this.markForPortalCreation.set(currentTicks + PORTAL_COOLDOWN);
        }


        player.endChallenge(this);
        this.challengeRoom.removeGravePos(player.lastGravePos);

        if (watchChallengersThread == null || !watchChallengersThread.isAlive()) {
            startWatchChallengers();
        }
    }

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
        if (!isClose) {
            if( this.watchChallengersThread != null && this.watchChallengersThread.isAlive())
                this.watchChallengersThread.interrupt();
            this.watchChallengersThread = null;
        }

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

        //clear portals
        this.deleteHomePortal();
        this.deleteChallengePortal();

        this.nearPlayers.clear();
        this.activePlayers.clear();
    }


    public void setMarkedForPortalCreationTime(long l) {
         markForPortalCreation.set(l);
    }
}
