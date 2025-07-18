package com.holybuckets.challengetemple.client;

import com.holybuckets.challengetemple.block.be.ChallengeChestBlockEntity;
import com.holybuckets.challengetemple.block.be.ChallengeChestCountingBlockEntity;
import com.holybuckets.challengetemple.block.be.ChallengeSingleUseChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;




public class ChestRenderers
{
    static class ChallengeChestRenderer extends ChestRenderer<ChallengeChestBlockEntity>
    {
        public ChallengeChestRenderer(BlockEntityRendererProvider.Context context) {
            super(context);
        }
    }

    static class ChallengeCountingChestRenderer extends ChestRenderer<ChallengeChestCountingBlockEntity>
    {
        public ChallengeCountingChestRenderer(BlockEntityRendererProvider.Context context) {
            super(context);
        }
    }

    static class ChallengeSingleUseChestRenderer extends ChestRenderer<ChallengeSingleUseChestBlockEntity>
    {
        public ChallengeSingleUseChestRenderer(BlockEntityRendererProvider.Context context) {
            super(context);
        }
    }

}
