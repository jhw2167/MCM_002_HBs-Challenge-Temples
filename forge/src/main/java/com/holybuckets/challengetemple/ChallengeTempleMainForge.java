package com.holybuckets.challengetemple;

import com.holybuckets.challengetemple.structure.GridStructurePlacement;
import net.blay09.mods.balm.api.Balm;
import net.minecraftforge.fml.common.Mod;

@Mod( com.holybuckets.challengetemple.Constants.MOD_ID)
public class ChallengeTempleMainForge {

    public ChallengeTempleMainForge() {
        super();
        Balm.initialize(Constants.MOD_ID, CommonClass::init);
        //DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initialize(Constants.MOD_ID, Client::initialize));
    }

}
