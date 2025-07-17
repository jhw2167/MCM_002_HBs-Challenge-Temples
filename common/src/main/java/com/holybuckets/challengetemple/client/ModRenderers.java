package com.holybuckets.challengetemple.client;

import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.block.ChallengeChestBlock;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.block.be.ChallengeChestBlockEntity;
import com.holybuckets.challengetemple.block.be.ModBlockEntities;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;


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
            ModBlockEntities.challengeChest::get, ChallengeChestRenderer::new );

    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

}

