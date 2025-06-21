package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.console.Messager;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.modelInterface.IManagedPlayer;
import com.holybuckets.foundation.player.ManagedPlayer;
import net.blay09.mods.balm.api.event.PlayerChangedDimensionEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.holybuckets.foundation.player.ManagedPlayer.registerManagedPlayerData;

public class ManagedChallenger implements IManagedPlayer {

    public static final String CLASS_ID = "010";

    static final Map<Player, ManagedChallenger> CHALLENGERS = new ConcurrentHashMap<>();

    Player p;
    static {
        registerManagedPlayerData(
                ManagedChallenger.class,
                () -> new ManagedChallenger( null)
        );
    }

    public ManagedChallenger(Player player) {
        this.p = player;
    }

    public static void init(EventRegistrar reg) {
        reg.registerOnPlayerChangedDimension(ManagedChallenger::onPlayerChangeDimension);
        reg.registerOnBreakBlock( arg -> System.out.println("breakBlock"));
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
        String msg = String.format("Beginning challenge: %s ,id: %s ",
            "<TITLE>",
            managedTemple.getChallengeRoom().getChallengeId()
        );
        LoggerProject.logDebug("010012", msg);
        Messager.getInstance().sendChat( (ServerPlayer) p, msg );

        //2. Clear Inventory

        //3. Set player spawn to inside the temple

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
