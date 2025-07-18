package com.holybuckets.challengetemple;

import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.block.be.ModBlockEntities;
import com.holybuckets.challengetemple.client.FabricChallengeBlockItemRenderer;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.blay09.mods.balm.api.client.BalmClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import com.holybuckets.challengetemple.client.ChallengeItemBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

//YOU NEED TO UPDATE NAME OF MAIN CLASS IN fabric.mod.json
//Use mod_id of other mods to add them in depends section, ensures they are loaded first
public class ChallengeTempleMainClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initialize(Constants.MOD_ID, CommonClass::initClient);

        //Rendering
        BuiltinItemRendererRegistry.INSTANCE.register(
            ModBlocks.challengeChest.asItem(),
            new FabricChallengeBlockItemRenderer( ModBlockEntities.challengeChest.get(),
                ModBlocks.challengeChest.defaultBlockState())
        );

        BuiltinItemRendererRegistry.INSTANCE.register(
            ModBlocks.challengeCountingChest.asItem(),
            new FabricChallengeBlockItemRenderer( ModBlockEntities.challengeCountingChest.get(),
                ModBlocks.challengeCountingChest.defaultBlockState())
        );

        BuiltinItemRendererRegistry.INSTANCE.register(
            ModBlocks.challengeSingleUseChest.asItem(),
            new FabricChallengeBlockItemRenderer( ModBlockEntities.challengeSingleUseChest.get(),
                ModBlocks.challengeSingleUseChest.defaultBlockState())
        );
    }
}
