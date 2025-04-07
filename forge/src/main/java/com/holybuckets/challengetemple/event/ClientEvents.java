package com.holybuckets.challengetemple.event;

import com.google.common.eventbus.Subscribe;
import com.holybuckets.challengetemple.block.ModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = com.holybuckets.challengetemple.Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.challengeGlass, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.challengeGlassPane, RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.challengeInvisibleBrick, RenderType.translucent());
    }

}
