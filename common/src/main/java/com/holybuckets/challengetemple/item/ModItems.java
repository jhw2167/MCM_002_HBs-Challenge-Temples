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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.holybuckets.foundation.item.ModItems.FOUNDATIONS_TAB;

public class ModItems {
    public static Item challengeBrickItem;

    public static void initialize(BalmItems items) {
        //items.registerItem(() -> challengeBrickItem = ModBlocks.challengeBrick.asItem(), id("challenge_brick"),
            //com.holybuckets.foundation.item.ModItems.FOUNDATIONS_TAB);
        items.registerCreativeModeTab(() -> new ItemStack(ModBlocks.challengeBrick), id("creative_tab"));
        items.addToCreativeModeTab(id("creative_tab"), ModItems::getItemLikeArray);
    }

    public static void clientInit() {
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
                ModBlocks.challengeBed,
                ModBlocks.challengeBrick,
                ModBlocks.challengeChest,
                ModBlocks.challengeBrickSlab,
                ModBlocks.challengeFauxBrick,
                ModBlocks.challengeInvisibleBrick,
                ModBlocks.challengeGlowstone,
                ModBlocks.challengeGlass,
                ModBlocks.challengeGlassPane,
                //ModBlocks.challengeWood,
                ModBlocks.buildingBlock,
                ModBlocks.challengeStone,
                ModBlocks.challengeCobble,
                ModBlocks.challengeLog,

                ModBlocks.challengeLadder

        );

        blocks.addAll(buildBlocks);
        blocks.addAll(addBlocks);
        return blocks.toArray(new ItemLike[0]);
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

}
