package com.holybuckets.challengetemple;

import com.holybuckets.challengetemple.platform.Services;
import com.holybuckets.foundation.event.BalmEventRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;


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

        ChallengeTempleMain.INSTANCE = new ChallengeTempleMain();
        BalmEventRegister.registerEvents();
        BalmEventRegister.registerCommands();
        isInitialized = true;
    }

    /**
     * Description: Run sample tests methods
     */
    public static void sample()
    {

    }

}