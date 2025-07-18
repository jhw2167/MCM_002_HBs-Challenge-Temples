
package com.holybuckets.challengetemple.block.be;

import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.block.ModBlocks;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static DeferredObject<BlockEntityType<ChallengeChestBlockEntity>> challengeChest;
    public static DeferredObject<BlockEntityType<ChallengeChestCountingBlockEntity>> challengeCountingChest;
    public static DeferredObject<BlockEntityType<ChallengeSingleUseChestBlockEntity>> challengeSingleUseChest;

    public static void initialize(BalmBlockEntities blockEntities)
    {
        challengeChest =  blockEntities
            .registerBlockEntity( id("challenge_chest"), ChallengeChestBlockEntity::new,
                () -> new Block[]{ModBlocks.challengeChest} );

        challengeCountingChest = blockEntities.registerBlockEntity(id("challenge_chest_counting"),
                ChallengeChestCountingBlockEntity::new,
            () -> new Block[]{ModBlocks.challengeCountingChest} );

        challengeSingleUseChest = blockEntities.registerBlockEntity(id("challenge_chest_single_use"),
                ChallengeSingleUseChestBlockEntity::new,
            () -> new Block[]{ModBlocks.challengeSingleUseChest} );
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}
