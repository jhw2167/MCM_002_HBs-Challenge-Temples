package com.holybuckets.challengetemple.block;

import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.block.spawner.*;
import com.holybuckets.challengetemple.item.ChallengeChestItem;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.DyeColor;
import java.util.EnumMap;

public class ModBlocks {

    public static float CHALLENGE_BLOCK_STRENGTH = 1000000f;
    public static float CHALLENGE_BLOCK_STRENGTH_MINEABLE = 0.4f;
    public static float CHALLENGE_BLOCK_EXPL_RES = 1000000f;
    public static float CHALLENGE_BLOCK_EXPL_RES_BLASTABLE = 1.0f;

    public static Block challengeBlock;
    public static Block challengeBed;
    public static Block challengeBrick;
    public static Block randomBrick;
    public static Block skeletonBrick;
    public static Block zombieBrick;
    public static Block blazeBrick;
    public static Block endermanBrick;
    public static Block creeperBrick;
    public static Block piglinBrick;
    public static Block zombiePiglinBrick;
    public static Block witchBrick;
    public static Block dogBrick;
    public static Block catBrick;
    public static Block slimeBrick;
    public static Block spiderBrick;
    public static Block polarBrick;
    public static Block ghastBrick;
    public static Block pillagerBrick;
    public static Block vindicatorBrick;
    public static Block evokerBrick;
    public static Block ravagerBrick;
    public static Block challengePushableBrick;
    public static Block challengeBrickSlab;
    public static Block challengeGlowstone;
    public static Block challengeGrass;
    public static Block challengeGlass;
    public static Block challengePushableGlass;
    public static Block challengeGlassPane;
    public static Block challengeFauxBrick;
    public static Block challengePushableFauxBrick;
    public static Block challengeInvisibleBrick;
    public static Block challengePushableInvisibleBrick;

    public static Block challengeWood;
    public static Block challengeStone;
    public static Block challengeCobble;
    public static Block challengeLog;

    //public static DeferredObject<Block> challengeChest;
    public static Block challengeChest;
    public static Block challengeCountingChest;
    public static Block challengeSingleUseChest; 
    public static Block challengeLadder;
    public static Block challengeBuildingBlock;
    public static Block challengeDoor;
    public static Block challengeGoldPlate;
    public static Block challengeClearingPlate;
    public static Block challengeStonePlate;
    public static Block challengeButton;
    public static Block challengeLever;
    public static Block challengeLava;
    public static Block challengeTrapdoor;
    public static Block challengeStairs;
    public static Block challengePushableStairs;
    public static Block challengeFence;
    public static Block challengeFenceGate;
    public static Block challengeLamp;
    public static final EnumMap<DyeColor, Block> BUILDING_BLOCKS = new EnumMap<>(DyeColor.class);



    public static void initialize(BalmBlocks blocks)
    {
        blocks.register(() -> challengeBlock = new ChallengeBlock(), () -> itemBlock(challengeBlock), id("challenge_"));
        blocks.register(() -> challengeBed = new ChallengeBed(), () -> itemBlock(challengeBed), id("challenge_bed"));
        blocks.register(() -> challengeBrick = new ChallengeBrick(), () -> itemBlock(challengeBrick), id("challenge_brick"));
        blocks.register(() -> randomBrick = new RandomBrick(), () -> itemBlock(randomBrick), id("random_brick"));
        blocks.register(() -> challengePushableBrick = new ChallengePushableBrick(), () -> itemBlock(challengePushableBrick), id("challenge_pushable_brick"));
        blocks.register(() -> challengeGlowstone = new ChallengeGlowstone(), () -> itemBlock(challengeGlowstone), id("challenge_glowstone"));
        blocks.register(() -> challengeGrass = new ChallengeGrass(), () -> itemBlock(challengeGrass), id("challenge_grass"));
        blocks.register(() -> challengeBrickSlab = new ChallengeBrickSlab(), () -> itemBlock(challengeBrickSlab), id("challenge_brick_slab"));
        blocks.register(() -> challengeGlass = new ChallengeGlass(), () -> itemBlock(challengeGlass), id("challenge_glass"));
        blocks.register(() -> challengePushableGlass = new ChallengePushableGlass(), () -> itemBlock(challengePushableGlass), id("challenge_pushable_glass"));
        blocks.register(() -> challengeGlassPane = new ChallengeGlassPane(), () -> itemBlock(challengeGlassPane), id("challenge_glass_pane"));

        blocks.register(() -> challengeFauxBrick = new ChallengeFauxBrick(), () -> itemBlock(challengeFauxBrick), id("challenge_faux_brick"));
        blocks.register(() -> challengePushableFauxBrick = new ChallengePushableFauxBrick(), () -> itemBlock(challengePushableFauxBrick), id("challenge_pushable_faux_brick"));
        blocks.register(() -> challengeInvisibleBrick = new ChallengeInvisibleBrick(), () -> itemBlock(challengeInvisibleBrick), id("challenge_invisible_brick"));
        blocks.register(() -> challengePushableInvisibleBrick = new ChallengePushableInvisibleBrick(), () -> itemBlock(challengePushableInvisibleBrick), id("challenge_pushable_invisible_brick"));
        blocks.register(() -> challengeStairs = new ChallengeStairs(), () -> itemBlock(challengeStairs), id("challenge_stairs"));
        blocks.register(() -> challengePushableStairs = new ChallengePushableStairs(), () -> itemBlock(challengePushableStairs), id("challenge_pushable_stairs"));

        blocks.register(() -> challengeStone = new ChallengeStone(), () -> itemBlock(challengeStone), id("challenge_stone"));
        blocks.register(() -> challengeCobble = new ChallengeCobble(), () -> itemBlock(challengeCobble), id("challenge_cobble"));
        blocks.register(() -> challengeLog = new ChallengeLog(), () -> itemBlock(challengeLog), id("challenge_log"));
        blocks.register(() -> challengeWood = new ChallengeWood(), () -> itemBlock(challengeWood), id("challenge_wood"));

        //DeferredObject<Block> registerBlock(Function<ResourceLocation, Block> supplier, ResourceLocation identifier);
        //challengeChest = blocks.registerBlock( loc -> new ChallengeChestBlock()  , id("challenge_chest"));
        blocks.register(() -> challengeChest = new ChallengeChestBlock(), () -> new ChallengeChestItem(challengeChest, new Properties()), id("challenge_chest"));
        blocks.register(() -> challengeCountingChest = new ChallengeChestCountingBlock(), () -> new ChallengeChestItem(challengeCountingChest, new Properties()), id("challenge_chest_counting"));
        blocks.register(() -> challengeSingleUseChest = new ChallengeChestSingleUseBlock(), () -> new ChallengeChestItem(challengeSingleUseChest, new Properties()), id("challenge_chest_single_use"));
        blocks.register(() -> challengeLadder = new ChallengeLadder(), () -> itemBlock(challengeLadder), id("challenge_ladder"));
            
        blocks.register(() -> challengeDoor = new ChallengeDoor(), () -> itemBlock(challengeDoor), id("challenge_door"));
        blocks.register(() -> challengeGoldPlate = new ChallengePressurePlates.ChallengeLightPlate(), () -> itemBlock(challengeGoldPlate), id("challenge_gold_plate"));
        blocks.register(() -> challengeClearingPlate = new ChallengePressurePlates.ChallengeClearingPlate(), () -> itemBlock(challengeClearingPlate), id("challenge_clearing_plate"));

        blocks.register(() -> challengeStonePlate = new ChallengePressurePlates.ChallengePressurePlate(), () -> itemBlock(challengeStonePlate), id("challenge_stone_plate"));
        blocks.register(() -> challengeButton = new ChallengeButton(), () -> itemBlock(challengeButton), id("challenge_button"));
        blocks.register(() -> challengeLever = new ChallengeLever(), () -> itemBlock(challengeLever), id("challenge_lever"));
            
        blocks.register(() -> challengeLava = new ChallengeLava(), // TODO: Add proper fluid registration
            () -> itemBlock(challengeLava),
            id("challenge_lava"));
            
        blocks.register(() -> challengeTrapdoor = new ChallengeTrapdoor(),
            () -> itemBlock(challengeTrapdoor),
            id("challenge_trapdoor"));

        blocks.register(() -> challengeFence = new ChallengeFence(), () -> itemBlock(challengeFence), id("challenge_fence"));
        blocks.register(() -> challengeFenceGate = new ChallengeFenceGate(), () -> itemBlock(challengeFenceGate), id("challenge_fence_gate"));
        blocks.register(() -> challengeLamp = new ChallengeLamp(), () -> itemBlock(challengeLamp), id("challenge_lamp"));

        // Register building blocks for each color
        blocks.register(
            () -> challengeBuildingBlock = new ChallengeBuildingBlock(),
            () -> itemBlock(challengeBuildingBlock),
            id("challenge_building_block")
        );

        //Spawner blocks
        blocks.register(() -> skeletonBrick = new SkeletonBrick(), () -> itemBlock(skeletonBrick), id("skeleton_brick"));
        blocks.register(() -> zombieBrick = new ZombieBrick(), () -> itemBlock(zombieBrick), id("zombie_brick"));
        blocks.register(() -> blazeBrick = new BlazeBrick(), () -> itemBlock(blazeBrick), id("blaze_brick"));
        blocks.register(() -> endermanBrick = new EndermanBrick(), () -> itemBlock(endermanBrick), id("enderman_brick"));
        blocks.register(() -> creeperBrick = new CreeperBrick(), () -> itemBlock(creeperBrick), id("creeper_brick"));
        blocks.register(() -> piglinBrick = new PiglinBrick(), () -> itemBlock(piglinBrick), id("piglin_brick"));
        blocks.register(() -> zombiePiglinBrick = new ZombiePiglinBrick(), () -> itemBlock(zombiePiglinBrick), id("zombie_piglin_brick"));
        blocks.register(() -> witchBrick = new WitchBrick(), () -> itemBlock(witchBrick), id("witch_brick"));
        blocks.register(() -> dogBrick = new DogBrick(), () -> itemBlock(dogBrick), id("dog_brick"));
        blocks.register(() -> catBrick = new CatBrick(), () -> itemBlock(catBrick), id("cat_brick"));
        blocks.register(() -> slimeBrick = new SlimeBrick(), () -> itemBlock(slimeBrick), id("slime_brick"));
        blocks.register(() -> spiderBrick = new SpiderBrick(), () -> itemBlock(spiderBrick), id("spider_brick"));
        blocks.register(() -> polarBrick = new PolarBrick(), () -> itemBlock(polarBrick), id("polar_brick"));
        blocks.register(() -> ghastBrick = new GhastBrick(), () -> itemBlock(ghastBrick), id("ghast_brick"));
        blocks.register(() -> pillagerBrick = new PillagerBrick(), () -> itemBlock(pillagerBrick), id("pillager_brick"));
        blocks.register(() -> vindicatorBrick = new VindicatorBrick(), () -> itemBlock(vindicatorBrick), id("vindicator_brick"));
        blocks.register(() -> evokerBrick = new EvokerBrick(), () -> itemBlock(evokerBrick), id("evoker_brick"));
        blocks.register(() -> ravagerBrick = new RavagerBrick(), () -> itemBlock(ravagerBrick), id("ravager_brick"));

    }

    private static BlockItem itemBlock(Block block) {
        return new BlockItem(block, new Properties() );
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }




}
