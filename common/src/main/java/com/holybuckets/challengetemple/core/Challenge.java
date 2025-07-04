package com.holybuckets.challengetemple.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.holybuckets.foundation.HBUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.Item;

import java.util.List;

/**
 * Stores settings and metadata specific to each challenge.
 */
public class Challenge {

    private String challengeId;
    private String author;
    private String challengeName;
    private String difficulty;
    private Vec3i size;
    private int totalPieces;

    private ChallengeRules challengeRules;
    private LootRules lootRules;


    public static class ChallengeRules {
        int playerMaxHearts;               
        int maxDeaths;                     
        boolean resetBlocksOnPlayerDeath;  
        boolean resetChestsOnPlayerDeath;  
        boolean clearInventoryOnPlayerDeath; 
        boolean playerDropsInventoryOnDeath; 

        // Public getters
        public int getPlayerMaxHearts() { return playerMaxHearts; }
        public int getMaxDeaths() { return maxDeaths; }
        public boolean isResetBlocksOnPlayerDeath() { return resetBlocksOnPlayerDeath; }
        public boolean isResetChestsOnPlayerDeath() { return resetChestsOnPlayerDeath; }
        public boolean isClearInventoryOnPlayerDeath() { return clearInventoryOnPlayerDeath; }
        public boolean isPlayerDropsInventoryOnDeath() { return playerDropsInventoryOnDeath; }
    }

    public static class LootRules {
        String lootPool;                
        List<Item> specificLoot;

        public String getLootPool() { return lootPool; }
        public List<Item> getSpecificLoot() { return specificLoot; }
    }

    // Public getters for main Challenge fields
    public String getChallengeId() { return challengeId; }
    public String getAuthor() { return author; }
    public String getChallengeName() { return challengeName; }
    public String getDifficulty() { return difficulty; }
    public Vec3i getSize() { return size; }
    public int getTotalPieces() { return totalPieces; }

    public ChallengeRules getChallengeRules() { return challengeRules; }
    public LootRules getLootRules() { return lootRules; }

    /**
     * Reads a Challenge from a JSON object.
     * @param json
     * @return
     */
    public static Challenge read(JsonObject json) {

        Challenge c = new Challenge();
        c.challengeId = json.get("challengeId").getAsString();
        c.author = json.get("author").getAsString();
        c.challengeName = json.get("challengeName").getAsString();
        c.difficulty = json.get("difficulty").getAsString();
        c.setSize(json.get("size"));

        //Challenge Rules
        ChallengeRules r = new ChallengeRules();
        c.challengeRules = r;
        JsonObject rules = json.getAsJsonObject("challengeRules");
        r.playerMaxHearts = rules.get("playerMaxHearts").getAsInt();
        r.maxDeaths = rules.get("maxDeaths").getAsInt();
        r.resetBlocksOnPlayerDeath = rules.get("resetBlocksOnPlayerDeath").getAsBoolean();
        r.resetChestsOnPlayerDeath = rules.get("resetChestsOnPlayerDeath").getAsBoolean();
        r.clearInventoryOnPlayerDeath = rules.get("clearInventoryOnPlayerDeath").getAsBoolean();
        r.playerDropsInventoryOnDeath = rules.get("playerDropsInventoryOnDeath").getAsBoolean();


        //Loot Rules
        LootRules l = new LootRules();
        c.lootRules = l;
        l.lootPool = json.get("lootPool").getAsString();
        c.setSpecificLoot(json.get("specificLoot"));

        return c;
    }

    private void setSize(JsonElement json)
    {
        if(json.isJsonNull()) return;
        String sz = json.getAsString();

        if(sz.equals("2x2")) {
            this.size = new Vec3i(32, 48, 32);
            this.totalPieces = 1;
        } else if(sz.equals("4x4")) {
            this.size = new Vec3i(64, 96, 64);
            this.totalPieces = 8;
        } else { //maybe 8x8
            this.size = new Vec3i(128, 96, 128);
            this.totalPieces = 16;
        }
    }

    private void setSpecificLoot(JsonElement lootArr)
    {
        List<Item> loot = List.of();
        this.lootRules.specificLoot = loot;
        if(lootArr.isJsonNull()) return;

        String[] items = lootArr.getAsString().split(",");
        for(String s : items) {
            //Item item = HBUtil.ItemUtil.itemNameToItem(s.trim());
            //if( item != null) loot.add(item);
        }

    }

}
