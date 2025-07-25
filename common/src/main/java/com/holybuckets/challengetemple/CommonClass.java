package com.holybuckets.challengetemple;

import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.block.be.ModBlockEntities;
import com.holybuckets.challengetemple.client.ModRenderers;
import com.holybuckets.challengetemple.client.screen.ModScreens;
import com.holybuckets.challengetemple.config.ChallengeTempleConfig;
import com.holybuckets.challengetemple.menu.ModMenus;
import com.holybuckets.challengetemple.platform.Services;
import com.holybuckets.challengetemple.item.ModItems;
import com.holybuckets.foundation.event.BalmEventRegister;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;


public class CommonClass {

    public static boolean isInitialized = false;
    public static void init()
    {
        if (isInitialized)
            return;

        //Initialize Foundations
        com.holybuckets.foundation.FoundationInitializers.commonInitialize();

        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {
            Constants.LOG.info("Hello to " + Constants.MOD_NAME + "!");
        }

        Balm.getConfig().registerConfig(ChallengeTempleConfig.class);
        ChallengeTempleMain.INSTANCE = new ChallengeTempleMain();
        BalmEventRegister.registerEvents();
        BalmEventRegister.registerCommands();
        ModBlocks.initialize(Balm.getBlocks());
        ModBlockEntities.initialize(Balm.getBlockEntities());
        ModItems.initialize(Balm.getItems());
        ModMenus.initialize(Balm.getMenus());


        isInitialized = true;
    }

    public static void initClient() {
        ModRenderers.clientInitialize(BalmClient.getRenderers());
        ModScreens.clientInitialize(BalmClient.getScreens());
        //ModItems.clientInit();
    }

    /**
     * Description: Run sample tests methods
     */
    public static void sample()
    {

    }


}