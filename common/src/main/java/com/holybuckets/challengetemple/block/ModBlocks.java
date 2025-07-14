package com.holybuckets.challengetemple.block;

import com.holybuckets.challengetemple.Constants;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static float CHALLENGE_BLOCK_STRENGTH = 1000000f;
    public static float CHALLENGE_BLOCK_EXPL_RES = 1f;


    public static Block challengeBed;
    public static Block challengeBrick;
    public static Block challengeBrickSlab;
    public static Block challengeGlowstone;
    public static Block challengeGlass;
    public static Block challengeGlassPane;
    public static Block challengeWood;
    public static Block challengeStone;
    public static Block challengeFauxBrick;
    public static Block challengeInvisibleBrick;

    //public static DeferredObject<Block> challengeChest;
    public static Block challengeChest;



    public static void initialize(BalmBlocks blocks) {
        blocks.register(() -> challengeBed = new ChallengeBed(), () -> itemBlock(challengeBed), id("challenge_bed"));
        blocks.register(() -> challengeBrick = new ChallengeBrick(), () -> itemBlock(challengeBrick), id("challenge_brick"));
        blocks.register(() -> challengeGlowstone = new ChallengeGlowstone(), () -> itemBlock(challengeGlowstone), id("challenge_glowstone"));
        blocks.register(() -> challengeBrickSlab = new ChallengeBrickSlab(), () -> itemBlock(challengeBrickSlab), id("challenge_brick_slab"));
        blocks.register(() -> challengeGlass = new ChallengeGlass(), () -> itemBlock(challengeGlass), id("challenge_glass"));
        blocks.register(() -> challengeGlassPane = new ChallengeGlassPane(), () -> itemBlock(challengeGlassPane), id("challenge_glass_pane"));
        //blocks.register(() -> challengeWood = new ChallengeWood(), () -> itemBlock(challengeWood), id("challenge_wood"));
        blocks.register(() -> challengeStone = new ChallengeStone(), () -> itemBlock(challengeStone), id("challenge_stone"));
        blocks.register(() -> challengeFauxBrick = new ChallengeFauxBrick(), () -> itemBlock(challengeFauxBrick), id("challenge_faux_brick"));
        blocks.register(() -> challengeInvisibleBrick = new ChallengeInvisibleBrick(), () -> itemBlock(challengeInvisibleBrick), id("challenge_invisible_brick"));

        //DeferredObject<Block> registerBlock(Function<ResourceLocation, Block> supplier, ResourceLocation identifier);
        //challengeChest = blocks.registerBlock( loc -> new ChallengeChestBlock()  , id("challenge_chest"));
        blocks.register(() -> challengeChest = new ChallengeChestBlock(), () -> itemBlock(challengeChest), id("challenge_chest"));

    }

    private static BlockItem itemBlock(Block block) {
        return new BlockItem(block, new Properties() );
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

}
