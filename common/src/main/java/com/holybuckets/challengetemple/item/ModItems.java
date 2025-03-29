package com.holybuckets.challengetemple.item;


import com.holybuckets.challengetemple.Constants;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.item.BalmItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ModItems {
    public static DeferredObject<CreativeModeTab> creativeModeTab;
    public static Item emptyBlockItem;

    public static void initialize(BalmItems items) {
        //items.registerItem(() -> emptyBlockItem = new EmptyBlockItem(items.itemProperties()), id("empty_block"));
        //creativeModeTab = items.registerCreativeModeTab(id(Constants.MOD_ID), () -> new ItemStack(ModBlocks.empty));
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

}
