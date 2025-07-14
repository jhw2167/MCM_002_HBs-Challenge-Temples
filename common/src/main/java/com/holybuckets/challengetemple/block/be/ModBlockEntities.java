package com.holybuckets.challengetemple.block.be;

import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.block.ModBlocks;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;

public class ModBlockEntities {

    public static DeferredObject<BlockEntityType<ChallengeChestBlockEntity>> challengeChest;

    public static void initialize(BalmBlockEntities blockEntities) {
        challengeChest =  blockEntities
            .registerBlockEntity( id("challenge_chest"), ChallengeChestBlockEntity::new,
            () -> new Block[]{ModBlocks.challengeChest} );
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}
