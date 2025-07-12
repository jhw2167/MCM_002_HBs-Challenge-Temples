package com.holybuckets.challengetemple.client;

import com.holybuckets.challengetemple.block.entity.ModBlockEntities;
import com.holybuckets.challengetemple.client.renderer.ChallengeBedBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ChallengeTempleClient {
    public static void initialize() {
        BlockEntityRenderers.register(ModBlockEntities.CHALLENGE_BED, ChallengeBedBlockEntityRenderer::new);
    }
}
