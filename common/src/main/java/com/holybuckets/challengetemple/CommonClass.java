package com.holybuckets.challengetemple;

import com.holybuckets.challengetemple.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;


public class CommonClass {

    public static boolean isInitialized = false;
    public static void init()
    {
        if (isInitialized)
            return;

        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {

            Constants.LOG.info("Hello to " + Constants.MOD_NAME + "!");
        }
        
        isInitialized = true;
        ChallengeTempleMain.INSTANCE = new ChallengeTempleMain();
    }

    /**
     * Description: Run sample tests methods
     */
    public static void sample()
    {

    }

}