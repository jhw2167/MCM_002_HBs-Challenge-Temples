package com.holybuckets.challengetemple.block;

import com.holybuckets.challengetemple.Constants;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static Block challengeBrick;
    public static Block challengeBrickSlab;
    public static Block challengeGlowstone;
    public static Block challengeGlass;
    public static Block challengeGlassPane;
    public static Block challengeWoodPlank;
    public static Block challengeWoodPlankSlab;

    public static void initialize(BalmBlocks blocks) {
        blocks.register(() -> challengeBrick = new ChallengeBrick(), () -> itemBlock(challengeBrick), id("challenge_brick"));
        blocks.register(() -> challengeGlowstone = new ChallengeGlowstone(), () -> itemBlock(challengeGlowstone), id("challenge_glowstone"));
        blocks.register(() -> challengeBrickSlab = new ChallengeBrickSlab(), () -> itemBlock(challengeBrickSlab), id("challenge_brick_slab"));
    }

    private static BlockItem itemBlock(Block block) {
        return new BlockItem(block, Balm.getItems().itemProperties());
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

    static BlockBehaviour.Properties defaultProperties() {
        return Balm.getBlocks().blockProperties().sound(SoundType.STONE).strength(5f, 2000f);
    }
}
