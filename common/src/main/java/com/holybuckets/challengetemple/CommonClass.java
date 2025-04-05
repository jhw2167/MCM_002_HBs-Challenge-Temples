package com.holybuckets.challengetemple;

import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.platform.Services;
import com.holybuckets.challengetemple.item.ModItems;
import com.holybuckets.foundation.event.BalmEventRegister;
import net.blay09.mods.balm.api.Balm;
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
        ModBlocks.initialize(Balm.getBlocks());
        ModItems.initialize(Balm.getItems());

        isInitialized = true;
    }

    /**
     * Description: Run sample tests methods
     */
    public static void sample()
    {

    }

}