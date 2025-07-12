package com.holybuckets.challengetemple.block.entity;

import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.blay09.mods.balm.api.DeferredRegister;
import net.blay09.mods.balm.api.block.BalmBlockEntities;

public class ModBlockEntities {
    public static BlockEntityType<ChallengeBedBlockEntity> CHALLENGE_BED;

    public static void initialize(BalmBlockEntities blockEntities) {
        blockEntities.register(() -> CHALLENGE_BED = BlockEntityType.Builder.of(
            ChallengeBedBlockEntity::new, ModBlocks.challengeBed).build(null),
            new ResourceLocation(Constants.MOD_ID, "challenge_bed"));
    }
}
