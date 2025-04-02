package com.holybuckets.challengetemple;

import com.holybuckets.challengetemple.core.TempleManager;
import com.holybuckets.challengetemple.portal.PortalApi;
import com.holybuckets.challengetemple.structure.GridStructurePlacement;
import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.event.EventRegistrar;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.LevelLoadingEvent;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.GenerationStep;

public class ChallengeTempleMain {

    public static final String CLASS_ID = "001";    //unused variable, value will be used for logging messages

    // Define mod id in a common place for everything to reference
    public static final String MODID = Constants.MOD_ID;
    public static final String NAME = "HBs Challenge Temples";
    public static final String VERSION = "1.0.0f";
    public static final Boolean DEBUG = false;

    public static ChallengeTempleMain INSTANCE;

    PortalApi portalApi;

    TempleManager templeManager;

    public ChallengeTempleMain()
    {
        super();
        init();
        INSTANCE = this;
        // LoggerProject.logInit( "001000", this.getClass().getName() ); // Uncomment if you have a logging system in place
    }

    private void init()
    {
        Constants.LOG.info("Initializing Challenge Temples mod with ID: {}", MODID);

        this.portalApi = (PortalApi) Balm.platformProxy()
            .withFabric("com.holybuckets.challengetemple.portal.FabricPortalApi")
            //.withForge("com.holybuckets.challengetemple.portal.ForgePortalApi")
            .build();

            EventRegistrar registrar = EventRegistrar.getInstance();
            TempleManager.init(registrar);

            //register events
            registrar.registerOnLevelLoad(this::onLevelLoad);
    }


    private void onLevelLoad(LevelLoadingEvent event) {
        Constants.LOG.info("Level loaded: {}", event.getLevel() );
        MinecraftServer server = GeneralConfig.getInstance().getServer();
        if(event.getLevel() != server.overworld()) return;

        this.templeManager = new TempleManager(event.getLevel());

    }


}
