package com.holybuckets.challengetemple.client;

import com.holybuckets.challengetemple.block.be.ChallengeChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;

public class ChallengeChestRenderer extends ChestRenderer<ChallengeChestBlockEntity>
{
    public ChallengeChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

}
