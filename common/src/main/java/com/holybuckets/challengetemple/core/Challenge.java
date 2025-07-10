package com.holybuckets.challengetemple.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.foundation.HBUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores settings and metadata specific to each challenge.
 */
public class Challenge {

    private String challengeId;
    private String author;
    private String challengeName;
    private List<Block> replaceEntityBlocks;
    private String difficulty;
    private Vec3i size;
    private int totalPieces;

    private ChallengeRules challengeRules;
    private LootRules lootRules;

    public List<Block> getReplaceEntityBlocks() {
        return replaceEntityBlocks;
    }


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
        int lootPool;
        List<ItemStack> specificLoot;

        public int getLootPool() { return lootPool; }
        public List<ItemStack> getSpecificLoot() { return specificLoot; }
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
        c.setReplaceEntityBlocks(json);
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
        JsonObject lootRules = json.getAsJsonObject("lootRules");
        l.lootPool = lootRules.get("lootPool").getAsInt();
        c.setSpecificLoot(lootRules.get("specificLoot"));

        return c;
    }

    private void setReplaceEntityBlocks(JsonObject json)
    {
        this.replaceEntityBlocks = new ArrayList<>();
        if(json.has("replaceStructureBlocksWith"))
        {
            JsonArray blocks = json.get("replaceStructureBlocksWith").getAsJsonArray();
            if(blocks.size() == 0) {
                //nothing
            } else if(blocks.size() == 1) {
                Block block = HBUtil.BlockUtil.blockNameToBlock(blocks.get(0).getAsString());
                if(block != null) this.replaceEntityBlocks.add(block);
            } else {
                for(JsonElement e : blocks) {
                    Block block = HBUtil.BlockUtil.blockNameToBlock(e.getAsString());
                    if(block != null) this.replaceEntityBlocks.add(block);
                }
            }
        }

        if(this.replaceEntityBlocks.size() < 1)
            this.replaceEntityBlocks.add(ModBlocks.challengeBrick);

        if(this.replaceEntityBlocks.size() < 2)
            this.replaceEntityBlocks.add(Blocks.AIR);
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
        this.lootRules.specificLoot = new ArrayList<>();
        if(lootArr.isJsonNull()) return;

        Map<Item, Integer> counts = new HashMap<>();
        String[] items = lootArr.getAsString().split(",");
        for(String s : items) {
            Item item = HBUtil.ItemUtil.itemNameToItem(s.trim());
            if(item == null) continue;
            if(counts.containsKey(item)) {
                counts.put(item, counts.get(item) + 1);
            } else {
                counts.put(item, 1);
            }
        }

        for(Map.Entry<Item, Integer> entry : counts.entrySet()) {
            ItemStack stack = new ItemStack(entry.getKey(), entry.getValue());
            this.lootRules.specificLoot.add(stack);
        }

    }

}
