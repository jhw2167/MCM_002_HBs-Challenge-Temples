package com.holybuckets.challengetemple;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class ChallengeTempleMainForge {

    public ChallengeTempleMainForge() {
        super();
        System.out.println("*************Challenge Temple Forge Mod Initializing*************");
        Balm.initialize(Constants.MOD_ID, CommonClass::init);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initialize(Constants.MOD_ID, ChallengeTempleMainForgeClient::clientInitializeForge));
    }


}
