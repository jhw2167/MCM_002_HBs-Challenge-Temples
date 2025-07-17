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
package com.holybuckets.challengetemple.block.be;

import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.block.ModBlocks;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static DeferredObject<BlockEntityType<ChallengeChestBlockEntity>> challengeChest;
    public static DeferredObject<BlockEntityType<ChallengeCountingChestBlockEntity>> challengeCountingChest;
    public static DeferredObject<BlockEntityType<ChallengeSingleUseChestBlockEntity>> challengeSingleUseChest;

    public static void initialize(BalmBlockEntities blockEntities) {
        challengeChest = blockEntities.registerBlockEntity("challenge_chest",
                ChallengeChestBlockEntity::new,
                () -> ModBlocks.challengeChest);

        challengeCountingChest = blockEntities.registerBlockEntity("challenge_counting_chest",
                ChallengeCountingChestBlockEntity::new,
                () -> ModBlocks.challengeCountingChest);

        challengeSingleUseChest = blockEntities.registerBlockEntity("challenge_single_use_chest",
                ChallengeSingleUseChestBlockEntity::new,
                () -> ModBlocks.challengeSingleUseChest);
    }
}
