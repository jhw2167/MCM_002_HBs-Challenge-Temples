package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.ChallengeTempleMain;
import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.console.Messager;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.modelInterface.IManagedPlayer;
import com.holybuckets.foundation.player.ManagedPlayer;
import net.blay09.mods.balm.api.event.BalmEvents;
import net.blay09.mods.balm.api.event.LivingDeathEvent;
import net.blay09.mods.balm.api.event.PlayerChangedDimensionEvent;
import net.blay09.mods.balm.api.event.UseBlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

    static {
        registerManagedPlayerData(
                ManagedChallenger.class,
                () -> new ManagedChallenger( null)
        );
    }

    public ManagedChallenger(Player player) {
        this.p = player;
        this.challengesTaken = new LinkedHashSet<>();
        this.templesEntered = new LinkedHashSet<>();
        this.challengesComplete = new LinkedHashSet<>();
    }

    public static void init(EventRegistrar reg) {
        reg.registerOnPlayerChangedDimension(ManagedChallenger::onPlayerChangeDimension);
        reg.registerOnUseBlock(ManagedChallenger::onPlayerUsedBlock);
        reg.registerOnServerTick( EventRegistrar.TickType.ON_20_TICKS, );
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
        new Thread(() -> challengerClearInventory()).start();

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

        private void challengerClearInventory()
        {

            //1. wait until player is in challegne dimension
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }

            //2. Clear the inventory multiple times, protecting against cheese
            final int TOTAL_CLEARS = 10;
            for (int i = 0; i < TOTAL_CLEARS; i++) {
                LoggerProject.logDebug("010013", "Clearing inventory: " + (i + 1) + "/" + TOTAL_CLEARS);
                ((ServerPlayer) p).getInventory().clearContent();
                try {
                    Thread.sleep(250); // wait a second before next clear
                } catch (InterruptedException e) {
                    return;
                }
            }

            //3. Clear items on the ground around the player

        }

    public void endChallenge(ManagedTemple managedTemple)
    {
        this.activeTemple = null;
        this.activeTempleInfo = null;
        this.holdInventory = null;

        this.setPlayerSpawn(managedTemple.getLevel(), this.originalSpawnPos, false);
        challengerClearInventory();
        ChallengeTempleMain.INSTANCE.inventoryApi.returnInventory((ServerPlayer) p, lastGravePos);
    }

    public void completedChallenge(ManagedTemple managedTemple) {
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
            challenger.onBlockUsed(e);
        }
    }

    private void onBlockUsed(UseBlockEvent e)
    {
        //Handle block used in challenge temple
        if (this.activeTemple == null) return;

        BlockPos hitPos = e.getHitResult().getBlockPos();
        BlockState hitBlockState = e.getLevel().getBlockState(hitPos);
        if(hitBlockState.equals( ModBlocks.challengeBed.defaultBlockState() ))
            this.setPlayerSpawn(CHALLENGE_LEVEL, hitPos.offset(0,1,0), true);
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
