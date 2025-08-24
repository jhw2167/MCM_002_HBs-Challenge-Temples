package com.holybuckets.challengetemple.item;


import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.block.be.ModBlockEntities;
import com.holybuckets.challengetemple.client.ChallengeItemBlockRenderer;
import net.blay09.mods.balm.api.item.BalmItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static Item challengeBrickItem;

    public static void initialize(BalmItems items) {
        //items.registerItem(() -> challengeBrickItem = ModBlocks.challengeBrick.asItem(), id("challenge_brick"),
            //com.holybuckets.foundation.item.ModItems.FOUNDATIONS_TAB);
        items.registerCreativeModeTab(() -> new ItemStack(ModBlocks.challengeBrick), id("creative_tab"));
        items.addToCreativeModeTab(id("creative_tab"), ModItems::getItemLikeArray);
    }

    public static void clientInitialize() {
        ChallengeItemBlockRenderer.CHEST_RENDERER = new ChallengeItemBlockRenderer(
                ModBlockEntities.challengeChest.get(),
                ModBlocks.challengeChest.defaultBlockState()
        );
    }

    private static ItemLike[] getItemLikeArray() {
        int i = 0;
        List<ItemLike> blocks =  new ArrayList<>();
        List<ItemLike> buildBlocks =  new ArrayList<>();
        for (DyeColor color : ModBlocks.BUILDING_BLOCKS.keySet()) {
            //buildBlocks.add( ModBlocks.BUILDING_BLOCKS.get(color) );
        }
        List<ItemLike> addBlocks = List.of(

            ModBlocks.challengeBlock,
            ModBlocks.challengeBed,
            ModBlocks.challengeBrick,
            ModBlocks.randomBrick,
            ModBlocks.challengePushableBrick,
            ModBlocks.challengeBrickSlab,
            ModBlocks.challengeFauxBrick,
            ModBlocks.challengePushableFauxBrick,
            ModBlocks.challengeStairs,
            ModBlocks.challengePushableStairs,
            ModBlocks.challengeInvisibleBrick,
            ModBlocks.challengePushableInvisibleBrick,
            ModBlocks.challengeGlowstone,
            ModBlocks.challengeGrass,
            ModBlocks.challengeGlass,
            ModBlocks.challengePushableGlass,
            ModBlocks.challengeGlassPane,
            ModBlocks.challengeFence,
            ModBlocks.challengeStone,
            ModBlocks.challengeCobble,

            ModBlocks.challengeLog,
            ModBlocks.challengeWood,
            ModBlocks.challengeLadder,
            ModBlocks.challengeBuildingBlock,

            ModBlocks.challengeChest,
            ModBlocks.challengeCountingChest,
            ModBlocks.challengeSingleUseChest,
            ModBlocks.challengeDoor,
            ModBlocks.challengeTrapdoor,
            ModBlocks.challengeFenceGate,
            ModBlocks.challengeStonePlate,
            ModBlocks.challengeGoldPlate,
            ModBlocks.challengeClearingPlate,
            ModBlocks.challengeButton,
            ModBlocks.challengeLever,
            ModBlocks.challengeLamp,
            ModBlocks.challengeLava
        );

        List<ItemLike> spawnerBlocks = List.of(
            ModBlocks.skeletonBrick,
            ModBlocks.zombieBrick,
            ModBlocks.blazeBrick,
            ModBlocks.endermanBrick,
            ModBlocks.creeperBrick,
            ModBlocks.piglinBrick,
            ModBlocks.zombiePiglinBrick,
            ModBlocks.witchBrick,
            ModBlocks.dogBrick,
            ModBlocks.catBrick,
            ModBlocks.slimeBrick,
            ModBlocks.spiderBrick,
            ModBlocks.polarBrick,
            ModBlocks.ghastBrick,
            ModBlocks.pillagerBrick,
            ModBlocks.vindicatorBrick,
            ModBlocks.evokerBrick,
            ModBlocks.ravagerBrick
        );

        List<ItemLike> vanillaBlocks = List.of(
            Blocks.OAK_BUTTON,
            Blocks.LEVER,
            Blocks.OAK_PRESSURE_PLATE,
            Blocks.PISTON,
            Blocks.STICKY_PISTON,
            Blocks.REPEATER,
            Blocks.DISPENSER,
            Blocks.DROPPER,
            Blocks.STONE,
            Blocks.OAK_FENCE,
            Blocks.NETHERRACK
        );

        blocks.addAll(buildBlocks);
        blocks.addAll(addBlocks);
        blocks.addAll(vanillaBlocks);
        blocks.addAll(spawnerBlocks);
        return blocks.toArray(new ItemLike[0]);
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

}

