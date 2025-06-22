package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.ChallengeTempleMain;
import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.console.Messager;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.modelInterface.IManagedPlayer;
import com.holybuckets.foundation.player.ManagedPlayer;
import net.blay09.mods.balm.api.event.PlayerChangedDimensionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.holybuckets.challengetemple.core.TempleManager.CHALLENGE_LEVEL;
import static com.holybuckets.foundation.player.ManagedPlayer.registerManagedPlayerData;

public class ManagedChallenger implements IManagedPlayer {

    public static final String CLASS_ID = "010";

    static final Map<Player, ManagedChallenger> CHALLENGERS = new ConcurrentHashMap<>();

    Player p;
    final LinkedHashSet<String> challengesTaken;
    final LinkedHashSet<String> templesEntered;
    final LinkedHashSet<String> challengesComplete;
    BlockPos lastGravePos;  //last position of grave where items were stored

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
    }

    //** CORE

    /**
     * 1. Send a message to the player giving challenge details
     * 2. Clear players inventory, and store it
     * 3. Set players spawn to the beginning of the challenge
     *
     * @param managedTemple
     */
    public void startChallenge(ManagedTemple managedTemple)
    {
        //1. Message Player
        String msg = String.format("%s started challenge: [%s] %s ",
            p.getDisplayName().getString(),
            managedTemple.getChallengeRoom().getChallengeId(),
            "<TITLE>"
        );
        LoggerProject.logDebug("010012", msg);
        Messager.getInstance().sendChat( (ServerPlayer) p, msg );

        //2. Clear Inventory
        new Thread(() -> challengerClearInventory(managedTemple)).start();

        //3. Set player spawn to inside the temple

    }

        private void challengerClearInventory(ManagedTemple managedTemple)
        {

            //1. wait until player is in challegne dimension
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }

            if (p.level() != CHALLENGE_LEVEL) {
                LoggerProject.logDebug("000000", "Player in dimension " + p.level());
            }


            lastGravePos = managedTemple.getChallengeRoom().addGrave(this);

            //2. Clear the inventory multiple times, protecting against cheese
            final int TOTAL_CLEARS = 10;
            for (int i = 0; i < TOTAL_CLEARS; i++) {
                LoggerProject.logDebug("010013", "Clearing inventory: " + (i + 1) + "/" + TOTAL_CLEARS);
                p.getInventory().
                try {
                    Thread.sleep(100); // wait a second before next clear
                } catch (InterruptedException e) {
                    return;
                }
            }

            //3. Clear items on the ground around the player

            //4. Collect data
            challengesTaken.add(managedTemple.getChallengeRoom().getChallengeId());
            templesEntered.add(managedTemple.getTempleId());
        }

    public void endChallenge(ManagedTemple managedTemple) {
        //2. Restore inventory
        ChallengeTempleMain.INSTANCE.inventoryApi.returnInventory((ServerPlayer) p, lastGravePos);
    }

    public void completedChallenge(ManagedTemple managedTemple) {
        challengesComplete.add(managedTemple.getChallengeRoom().getChallengeId());
        this.endChallenge(managedTemple);
    }

    //** EVENTS
    public static void onPlayerChangeDimension(PlayerChangedDimensionEvent event) {
        LoggerProject.logDebug(
            "010011",
            "onPlayerChangeDimension"
        );

        TempleManager.onPlayerChangeDimension(event,
            CHALLENGERS.get(event.getPlayer()));
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
    }

    @Override
    public void handlePlayerLeave(Player player) {

    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {

    }

    @Override
    public void setId(String s) {

    }

    @Override
    public void setPlayer(Player player) {
        if(player != null) {
            this.p = player;
            CHALLENGERS.putIfAbsent(player, this);
        }
    }

    public Player getPlayer() {
        return p;
    }

    public ServerPlayer getServerPlayer() {
        return (ServerPlayer) p;
    }

}
