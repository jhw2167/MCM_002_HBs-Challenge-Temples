package com.holybuckets.challengetemple.block.be;

import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.block.ModBlocks;
import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;

public class ModBlockEntityTypes {
    public static DeferredObject<BlockEntityType<ChallengeChestBlockEntity>> CHALLENGE_CHEST;

    public static void initialize() {
        CHALLENGE_CHEST = BlockEntityType.Builder.of(ChallengeChestBlockEntity::new, ModBlocks.challengeChest)
            .build(null)
            .registeredAs(new ResourceLocation(Constants.MOD_ID, "challenge_chest"));
    }
}
