package com.holybuckets.challengetemple.client;

import com.holybuckets.challengetemple.block.ModBlocks;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.ChestRenderer;


public class ModRenderers {

    public static void clientInitialize(BalmRenderers renderers) {
        renderers.setBlockRenderType(() -> ModBlocks.challengeGlass, RenderType.cutout() );
        renderers.setBlockRenderType(() -> ModBlocks.challengeGlass, RenderType.translucent() );

        renderers.setBlockRenderType(() -> ModBlocks.challengeGlassPane, RenderType.cutout() );
        renderers.setBlockRenderType(() -> ModBlocks.challengeGlassPane, RenderType.translucent() );

        renderers.setBlockRenderType(() -> ModBlocks.challengeInvisibleBrick, RenderType.cutout() );
        renderers.setBlockRenderType(() -> ModBlocks.challengeInvisibleBrick, RenderType.translucent() );

    }

}
