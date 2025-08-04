package com.holybuckets.challengetemple;

import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.client.ChallengeItemBlockRenderer;
import com.holybuckets.challengetemple.client.CommonClassClient;
import com.holybuckets.challengetemple.client.IBewlrRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;

@Mod( Constants.MOD_ID)
public class ChallengeTempleMainForgeClient {


    public static void clientInitializeForge() {
        CommonClassClient.initClient();
        //Item challengeChest = ModBlocks.challengeChest.asItem();
       // setBlockEntityRender( challengeChest, ChallengeItemBlockRenderer.CHEST_RENDERER);
    }

        private static void setBlockEntityRender(Object item, BlockEntityWithoutLevelRenderer renderer) {
            ((IBewlrRenderer) item).setBlockEntityWithoutLevelRenderer(renderer);
        }

}
