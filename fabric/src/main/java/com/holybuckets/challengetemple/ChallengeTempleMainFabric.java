package com.holybuckets.challengetemple;

import net.blay09.mods.balm.api.Balm;
import net.fabricmc.api.ModInitializer;

//YOU NEED TO UPDATE NAME OF MAIN CLASS IN fabric.mod.json
//Use mod_id of other mods to add them in depends section, ensures they are loaded first
public class ChallengeTempleMainFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Balm.initialize(Constants.MOD_ID, CommonClass::init);
    }
}
