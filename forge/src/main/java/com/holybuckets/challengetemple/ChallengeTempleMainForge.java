package com.holybuckets.challengetemple;

import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.client.ChallengeItemBlockRenderer;
import com.holybuckets.challengetemple.client.IBewlrRenderer;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod( Constants.MOD_ID)
public class ChallengeTempleMainForge {

    public ChallengeTempleMainForge() {
        super();
        Balm.initialize(Constants.MOD_ID, CommonClass::init);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initialize(Constants.MOD_ID, CommonClass::initClient));
        //DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> clientInitializeForge());
    }


    public void clientInitializeForge() {

        //Register Client Rendering bewlrs
        Item challengeChest = ModBlocks.challengeChest.asItem();
        setBlockEntityRender( challengeChest, ChallengeItemBlockRenderer.CHEST_RENDERER);
    }

        private void setBlockEntityRender(Object item, BlockEntityWithoutLevelRenderer renderer) {
            ((IBewlrRenderer) item).setBlockEntityWithoutLevelRenderer(renderer);
        }

}
