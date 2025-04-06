package com.holybuckets.challengetemple;

import net.blay09.mods.balm.api.client.BalmClient;
import net.fabricmc.api.ClientModInitializer;

//YOU NEED TO UPDATE NAME OF MAIN CLASS IN fabric.mod.json
//Use mod_id of other mods to add them in depends section, ensures they are loaded first
public class ChallengeTempleMainClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initialize(Constants.MOD_ID, CommonClass::initClient);
    }
}
