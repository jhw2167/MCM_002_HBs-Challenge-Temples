package com.holybuckets.challengetemple.item;


import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.block.be.ModBlockEntities;
import com.holybuckets.challengetemple.client.ChallengeItemBlockRenderer;
import net.blay09.mods.balm.api.item.BalmItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

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
        return new ItemLike[]{
                ModBlocks.challengeBed,
                ModBlocks.challengeBrick,
                ModBlocks.challengeChest,
                ModBlocks.challengeBrickSlab,
                ModBlocks.challengeGlowstone,
                ModBlocks.challengeGlass,
                ModBlocks.challengeGlassPane,
                //ModBlocks.challengeWood,
                ModBlocks.challengeStone,
                ModBlocks.challengeCobble,
                ModBlocks.challengeFauxBrick,
                ModBlocks.challengeInvisibleBrick
        };
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

}
