package com.holybuckets.challengetemple.client;

import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.block.be.ModBlockEntities;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;


public class ModRenderers {

    public static void clientInitialize(BalmRenderers renderers) {
        renderers.setBlockRenderType(() -> ModBlocks.challengeGlass, RenderType.cutout() );
        renderers.setBlockRenderType(() -> ModBlocks.challengeGlass, RenderType.translucent() );

        renderers.setBlockRenderType(() -> ModBlocks.challengeGlassPane, RenderType.cutout() );
        renderers.setBlockRenderType(() -> ModBlocks.challengeGlassPane, RenderType.translucent() );

        renderers.setBlockRenderType(() -> ModBlocks.challengeInvisibleBrick, RenderType.cutout() );
        renderers.setBlockRenderType(() -> ModBlocks.challengeInvisibleBrick, RenderType.translucent() );

        renderers.setBlockRenderType(() -> ModBlocks.challengeLadder, RenderType.cutoutMipped() );

        renderers.setBlockRenderType(() -> ModBlocks.challengeDoor, RenderType.cutout() );
        renderers.setBlockRenderType(() -> ModBlocks.challengeTrapdoor, RenderType.cutout() );

        //default <T extends BlockEntity > void registerBlockEntityRenderer(ResourceLocation identifier, Supplier<BlockEntityType<T>> type,
        // BlockEntityRendererProvider<? super T> provider) {
        renderers.registerBlockEntityRenderer( id("challenge_chest"),
            ModBlockEntities.challengeChest::get, ChestRenderers.ChallengeChestRenderer::new );

        renderers.registerBlockEntityRenderer( id("challenge_chest_counting"),
            ModBlockEntities.challengeCountingChest::get, ChestRenderers.ChallengeCountingChestRenderer::new );

        renderers.registerBlockEntityRenderer( id("challenge_chest_single_use"),
            ModBlockEntities.challengeSingleUseChest::get, ChestRenderers.ChallengeSingleUseChestRenderer::new );

    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

}

