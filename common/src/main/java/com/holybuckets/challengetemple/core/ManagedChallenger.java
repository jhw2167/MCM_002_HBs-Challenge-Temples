package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.ChallengeTempleMain;
import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.console.Messager;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.ServerTickEvent;
import com.holybuckets.foundation.modelInterface.IManagedPlayer;
import net.blay09.mods.balm.api.event.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.holybuckets.challengetemple.core.ChallengeRoom.EXIT_PORTAL_BLOCK;
import static com.holybuckets.challengetemple.core.TempleManager.CHALLENGE_LEVEL;
import static com.holybuckets.foundation.player.ManagedPlayer.registerManagedPlayerData;

public class ManagedChallenger implements IManagedPlayer {

    public static final String CLASS_ID = "010";

    static final Map<Player, ManagedChallenger> CHALLENGERS = new ConcurrentHashMap<>();

    Player p;
    final LinkedHashSet<String> challengesTaken;
    final LinkedHashSet<String> templesEntered;
    final LinkedHashSet<String> challengesComplete;
    ManagedTemple activeTemple;
    ActiveTempleInfo activeTempleInfo; //serialize temple data
    BlockPos lastGravePos;  //last position of grave where items were stored
    BlockPos originalSpawnPos;
    BlockPos templeSpawnPos;
    List<Pair<Integer,ItemStack>> holdInventory;
    private Queue<Boolean> clearInventoryQueue = new LinkedList<>();

    static {
        registerManagedPlayerData(
                ManagedChallenger.class,
                () -> new ManagedChallenger( null)
        );
    }

    private boolean pendingInventoryReturn;

    public ManagedChallenger(Player player) {
        this.p = player;
        this.challengesTaken = new LinkedHashSet<>();
        this.templesEntered = new LinkedHashSet<>();
        this.challengesComplete = new LinkedHashSet<>();
        this.pendingInventoryReturn = false;
    }

    public static void init(EventRegistrar reg) {
        reg.registerOnPlayerChangedDimension(ManagedChallenger::onPlayerChangeDimension);
        reg.registerOnUseBlock(ManagedChallenger::onPlayerUsedBlock);
        reg.registerOnBreakBlock(ManagedChallenger::onPlayerBreakBlock);
        reg.registerOnServerTick(EventRegistrar.TickType.ON_20_TICKS, ManagedChallenger::onServer20Ticks );
        reg.registerOnServerTick(EventRegistrar.TickType.ON_SINGLE_TICK, ManagedChallenger::processClearInventory);
    }



    public static boolean isActiveChallenger(Player p) {
        if (p == null || !(p instanceof ServerPlayer)) return false;
        if(! CHALLENGERS.containsKey(p)) return false;
        return CHALLENGERS.get(p).activeTemple != null;
    }

    //** CORE
    public void setPlayerSpawn(Level level, BlockPos spawnPos, boolean setTempleSpawn)
    {
        ServerPlayer sp = (ServerPlayer) p;
        ResourceKey<Level> challengeLevelKey = level.dimension();
        sp.setRespawnPosition(challengeLevelKey,
            spawnPos,
            0, //rotation,
            true,   //force
            false); //skip spawn screen

        this.templeSpawnPos =  (setTempleSpawn) ? spawnPos : null;
        Messager.getInstance().sendChat(sp, String.format("Spawn set" + (setTempleSpawn ? "" : ": %s:%s"),
            challengeLevelKey.location(),
            spawnPos
        ));
    }


    /**
     * 1. Send a message to the player giving challenge details
     * 2. Clear players inventory, and store it
     * 3. Set players spawn to the beginning of the challenge
     *
     * @param managedTemple
     */
    public void startChallenge(ManagedTemple managedTemple)
    {
        ServerPlayer sp = (ServerPlayer) p;
        this.activeTemple = managedTemple;
        this.activeTempleInfo = new ActiveTempleInfo( managedTemple );


        //2. Clear Inventory
        lastGravePos = managedTemple.getChallengeRoom().addGrave(this);
        this.enqueueClearInventory();

        //3. Set player spawn to inside the temple
        this.originalSpawnPos = sp.getRespawnPosition();
        if(this.originalSpawnPos == null)
            this.originalSpawnPos = managedTemple.getLevel().getSharedSpawnPos();
        this.setPlayerSpawn(CHALLENGE_LEVEL.getLevel(),
             managedTemple.getChallengeRespawnPos(), true);

        //4. Collect data
        challengesTaken.add(managedTemple.getChallengeRoom().getChallengeId());
        templesEntered.add(managedTemple.getTempleId());

        //5. Message Player
        try {
        String msg = String.format("%s started challenge: [%s] %s\nSpawn set to temple",
            p.getDisplayName().getString(),
            managedTemple.getChallengeRoom().getChallengeId(),
            managedTemple.getChallengeRoom().getChallenge().getChallengeName(),
            ""//spawnPos.toShortString()
            );
        LoggerProject.logDebug("010012", msg);
        Messager.getInstance().sendChat( sp, msg );
        } catch (NullPointerException e) {
            String msg = String.format("%s started challenge with undefined name or id",
                p.getDisplayName().getString()
            );
            Messager.getInstance().sendChat( sp, msg );
        }

    }

    private static int CLEAR_RATE = 5;
    public synchronized void enqueueClearInventory() {
        // Add 40 clear inventory operations to the queue
        for (int i = 0; i < 41; i++) {
            clearInventoryQueue.offer(i % CLEAR_RATE == 0);
        }
    }

    private void challengerClearInventory() {
        //1. Clear player inventory
        p.getInventory().clearContent();

        //2. clear items on ground around player
        AABB area = new AABB(p.blockPosition()).inflate(5);
        for (ItemEntity item : CHALLENGE_LEVEL.getEntitiesOfClass(ItemEntity.class, area)) {
            item.discard();
        }
    }

    private void challengerReturnInventory() {
        ChallengeTempleMain.INSTANCE.inventoryApi.returnInventory((ServerPlayer) p, lastGravePos);
        this.pendingInventoryReturn = false;
    }


    private void onChallengerBlockUsed(UseBlockEvent e)
    {
        //Handle block used in challenge temple
        if (this.activeTemple == null) return;

        BlockPos hitPos = e.getHitResult().getBlockPos();
        BlockState hitBlockState = e.getLevel().getBlockState(hitPos);
        if(hitBlockState.equals( ModBlocks.challengeBed.defaultBlockState() ))
            this.setPlayerSpawn(CHALLENGE_LEVEL, hitPos.offset(0,1,0), true);
    }

    private void onChallengerBlockBreak(BreakBlockEvent e)
    {
        //Handle block used in challenge temple
        if (this.activeTemple == null) return;

        BlockState hitBlockState = e.getState();
        if(hitBlockState.equals( ModBlocks.challengeBed.defaultBlockState() )) {
            this.enqueueClearInventory();
            this.activeTemple.playerQuitChallenge(this);
        }

    }



    public void endChallenge(ManagedTemple managedTemple)
    {
        this.activeTemple = null;
        this.activeTempleInfo = null;
        this.holdInventory = null;

        String id = managedTemple.getChallengeRoom().getChallengeId();
        if(!this.challengesComplete.contains(id))
            this.enqueueClearInventory();
        this.setPlayerSpawn(managedTemple.getLevel(), this.originalSpawnPos, false);
        this.pendingInventoryReturn = true;
        Messager.getInstance().sendChat((ServerPlayer) this.p,
            String.format("Ending Challenge: %s, your inventory is being cleared",
            managedTemple.getChallengeRoom().getChallengeId()
        ));
    }

    public void completedChallenge(ManagedTemple managedTemple) {
        this.enqueueClearInventory();
        challengesComplete.add(managedTemple.getChallengeRoom().getChallengeId());
    }

    //** EVENTS
    public static void onPlayerChangeDimension(PlayerChangedDimensionEvent event) {
        LoggerProject.logDebug("010011", "onPlayerChangeDimension " + event.getPlayer().getDisplayName());

    try {
          TempleManager.onPlayerChangeDimension(event, CHALLENGERS.get(event.getPlayer()));
    } catch (Exception e) {
        LoggerProject.logError("010011", "Error handling player change dimension leaving challenge temple: " + e.getMessage());
        e.printStackTrace();
    }

    }

    public static void onServer20Ticks(ServerTickEvent event) {
        int i = 0;  //just for debugging
    }

    public static void processClearInventory(ServerTickEvent event)
    {
        for (ManagedChallenger c : CHALLENGERS.values())
        {
            if (c.clearInventoryQueue.isEmpty()) {
                if(c.pendingInventoryReturn)
                    c.challengerReturnInventory();
            } else if( c.clearInventoryQueue.poll() ) {
                c.challengerClearInventory();
            }
        }
    }

    public static void onPlayerUsedBlock(UseBlockEvent e)
    {
        ///Check if this occured in challenge level or we dont care
        if (e.getLevel() != CHALLENGE_LEVEL) return;

        Player player = e.getPlayer();
        if (player == null || !(player instanceof  ServerPlayer)) return;
        if (!e.getHand().equals(InteractionHand.MAIN_HAND)) return;

        ManagedChallenger challenger = CHALLENGERS.get(player);
        if (challenger == null) return;

        if (challenger.activeTemple != null) {
            challenger.onChallengerBlockUsed(e);
        }
    }


    private static void onPlayerBreakBlock(BreakBlockEvent breakBlockEvent)
    {
        if( breakBlockEvent.getLevel() != CHALLENGE_LEVEL) return;

        Player player = breakBlockEvent.getPlayer();
        if (player == null || !(player instanceof ServerPlayer)) return;
        if (!CHALLENGERS.containsKey(player)) return;

        ManagedChallenger challenger = CHALLENGERS.get(player);
        if (challenger.activeTemple != null) {
           challenger.onChallengerBlockBreak(breakBlockEvent);
        }
    }


    //** OVERRIDES

    @Override
    public boolean isServerOnly() {
        return true;
    }

    @Override
    public boolean isClientOnly() {
        return false;
    }

    @Override
    public boolean isInit(String s) {
        return false;
    }

    @Override
    public IManagedPlayer getStaticInstance(Player player, String id) {
        if(!(player instanceof ServerPlayer)) {
            return null;
        }
        return CHALLENGERS.get(player);
    }

    @Override
    public void handlePlayerJoin(Player player) {
        if(p == player && this.activeTempleInfo != null)
            this.activeTemple = TempleManager.handlePlayerJoinedInTemple( (ServerPlayer) p,
                 activeTempleInfo.level, activeTempleInfo.templeId );
    }

    @Override
    public void handlePlayerLeave(Player player) {
        if(p == player && this.activeTemple != null) {
            this.activeTemple.onPlayerLeave( (ServerPlayer) player );
        }
        CHALLENGERS.remove(player);
    }

    @Override
    public void handlePlayerRespawn(Player player)
    {
        if(this.activeTemple == null) return;

        this.setPlayerSpawn(CHALLENGE_LEVEL, this.templeSpawnPos, true);

        if(this.holdInventory == null) return;
        ServerPlayer sp = (ServerPlayer) this.p;
        Inventory inv = sp.getInventory();
        this.holdInventory.stream().forEach( p -> { inv.setItem(p.getLeft(), p.getRight()); });

    }

    @Override
    public void handlePlayerDeath(Player player)
    {
        if(this.activeTemple == null) return;

        Challenge c = this.activeTemple.getChallengeRoom().getChallenge();

        this.holdInventory = null;
        if( c.getChallengeRules().playerDropsInventoryOnDeath ) {
            this.p.getInventory().dropAll();
        }
        if(c.getChallengeRules().keepInventoryOnPlayerDeath)
        {
            Inventory inv = player.getInventory();
            this.holdInventory = new ArrayList<>(512);
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if ( stack == null || stack.isEmpty()) {

                } else {
                    this.holdInventory.add(Pair.of(i, stack.copy()));
                }
            }
        } else {
            this.p.getInventory().clearContent();
        }

        this.activeTemple.playerDiedInChallenge(this);
    }

    @Override
    public void handlePlayerAttack(Player player, Entity entity) {
        //nothing
    }

    @Override
    public void handlePlayerDigSpeed(Player player, float v, Float aFloat) {
        //nothing
    }

    @Override
    public void setId(String s) {

    }

    @Override
    public void setPlayer(Player player)
    {
        if(this.p == null) {
            this.p = player;
            CHALLENGERS.put(player, this);
            return;
        }
        ManagedChallenger challenger = CHALLENGERS.remove(this.p);
        this.p = player;
        CHALLENGERS.put(player, challenger);
    }

    public Player getPlayer() {
        return p;
    }

    public ServerPlayer getServerPlayer() {
        return (ServerPlayer) p;
    }

    public ManagedTemple getActiveTemple() {
        return activeTemple;
    }

    @Override
    public CompoundTag serializeNBT() {
        //serialize lastGravePos and challenges taken
        CompoundTag compoundTag = new CompoundTag();
        if(lastGravePos != null) {
            String pos = HBUtil.BlockUtil.positionToString(lastGravePos);
            compoundTag.putString("lastGravePos", pos);
        }

        if(originalSpawnPos != null) {
            String pos = HBUtil.BlockUtil.positionToString(originalSpawnPos);
            compoundTag.putString("originalSpawnPos", pos);
        }

        if(templeSpawnPos != null) {
            String pos = HBUtil.BlockUtil.positionToString(templeSpawnPos);
            compoundTag.putString("templeSpawnPos", pos);
        }

        if(this.activeTempleInfo != null) {
            compoundTag.putString("activeTempleInfo", this.activeTempleInfo.serialize());
        }

        String challengesTaken = "";
        if( !this.challengesTaken.isEmpty())
            challengesTaken = String.join("&", this.challengesTaken);
        compoundTag.putString("challengesTaken", challengesTaken);

        String challengesComplete = "";
        if( !this.challengesComplete.isEmpty())
            challengesComplete = String.join("&", this.challengesComplete);
        compoundTag.putString("challengesComplete", challengesComplete);


        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag)
    {
        //deserialize the data
        if (compoundTag.contains("lastGravePos"))
        {
            String posString = compoundTag.getString("lastGravePos");
            Vec3i pos = HBUtil.BlockUtil.stringToBlockPos(posString);
            this.lastGravePos = (pos != null) ? new BlockPos(pos) : null;
        }

        if (compoundTag.contains("originalSpawnPos"))
        {
            String posString = compoundTag.getString("originalSpawnPos");
            Vec3i pos = HBUtil.BlockUtil.stringToBlockPos(posString);
            this.originalSpawnPos = (pos != null) ? new BlockPos(pos) : null;
        }

        if (compoundTag.contains("templeSpawnPos"))
        {
            String posString = compoundTag.getString("templeSpawnPos");
            Vec3i pos = HBUtil.BlockUtil.stringToBlockPos(posString);
            this.templeSpawnPos = (pos != null) ? new BlockPos(pos) : null;
        }

        if (compoundTag.contains("activeTempleInfo")) {
            String activeTempleData = compoundTag.getString("activeTempleInfo");
            this.activeTempleInfo = ActiveTempleInfo.deserialize(activeTempleData);
        }

        if (compoundTag.contains("challengesTaken"))
        {
            String challenges = compoundTag.getString("challengesTaken");
            if (!challenges.isEmpty()) {
                String[] challengesArray = challenges.split("&");
                for (String challenge : challengesArray) {
                    this.challengesTaken.add(challenge);
                }
            }
        }

        if (compoundTag.contains("challengesComplete"))
        {
            String challenges = compoundTag.getString("challengesComplete");
            if (!challenges.isEmpty()) {
                String[] challengesArray = challenges.split("&");
                for (String challenge : challengesArray) {
                    this.challengesComplete.add(challenge);
                }
            }
        }


    }

    //write a private class ActiveTempleInfo that contains templeId and levelId both as strings
    //they support deserialize and serialize operations to and from string
    private static class ActiveTempleInfo {
        private String templeId;
        private Level level;

        public ActiveTempleInfo(String templeId, Level level) {
            this.templeId = templeId;
            this.level = level;
        }

        public ActiveTempleInfo(String templeId, String levelId) {
            this(templeId, HBUtil.LevelUtil.toLevel(HBUtil.LevelUtil.LevelNameSpace.SERVER, levelId));
        }

        public ActiveTempleInfo(ManagedTemple temple) {
            this.templeId = temple.getTempleId();
            this.level = temple.getLevel();
        }

        public String serialize() {
            return templeId + "&" + HBUtil.LevelUtil.toLevelId(level);
        }

        public static ActiveTempleInfo deserialize(String data) {
            String[] parts = data.split("&");
            if (parts.length != 2) return null;
            return new ActiveTempleInfo(parts[0], parts[1]);
        }
    }

}
