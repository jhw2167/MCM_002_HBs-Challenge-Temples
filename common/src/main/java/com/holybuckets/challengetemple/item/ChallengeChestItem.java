package com.holybuckets.challengetemple.item;

import com.holybuckets.challengetemple.client.ChallengeItemBlockRenderer;
import com.holybuckets.challengetemple.client.IBewlrRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChallengeChestItem extends BlockItem {
    public ChallengeChestItem(Block block, Properties properties) {
        super(block, properties);
    }

}
