package com.holybuckets.challengetemple.item;


import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.block.ModBlocks;
import net.blay09.mods.balm.api.item.BalmItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import static com.holybuckets.foundation.item.ModItems.FOUNDATIONS_TAB;

public class ModItems {
    public static Item challengeBrickItem;

    public static void initialize(BalmItems items) {
        //items.registerItem(() -> challengeBrickItem = ModBlocks.challengeBrick.asItem(), id("challenge_brick"),
            //com.holybuckets.foundation.item.ModItems.FOUNDATIONS_TAB);

        items.addToCreativeModeTab(FOUNDATIONS_TAB, () -> new ItemLike[]{
                ModBlocks.challengeBrick
        });
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

}
