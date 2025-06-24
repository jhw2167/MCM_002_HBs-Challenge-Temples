package com.holybuckets.challengetemple;

import com.holybuckets.challengetemple.core.ManagedChallenger;
import com.holybuckets.challengetemple.core.TempleManager;
import com.holybuckets.challengetemple.externalapi.InventoryApi;
import com.holybuckets.challengetemple.externalapi.PortalApi;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.LevelLoadingEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

//net.minecraft.server.commands.LocateCommand;
public class ChallengeTempleMain {

    public static final String CLASS_ID = "001";    //unused variable, value will be used for logging messages

    // Define mod id in a common place for everything to reference
    public static final String MODID = Constants.MOD_ID;
    public static final String NAME = "HBs Challenge Temples";
    public static final String VERSION = "1.0.0f";
    public static final Boolean DEBUG = true;

    public static ChallengeTempleMain INSTANCE;

    PortalApi portalApi;
    public InventoryApi inventoryApi;
    TempleManager templeManager;
    ServerLevel challengeDimension;

    public ChallengeTempleMain()
    {
        super();
        init();
        INSTANCE = this;
        // LoggerProject.logInit( "001000", this.getClass().getName() ); // Uncomment if you have a logging system in place
    }

    private void init()
    {

        this.portalApi = (PortalApi) Balm.platformProxy()
            .withFabric("com.holybuckets.challengetemple.externalapi.FabricPortalApi")
            .withForge("com.holybuckets.challengetemple.externalapi.ForgePortalApi")
            .build();

        this.inventoryApi = (InventoryApi) Balm.platformProxy()
        .withFabric("com.holybuckets.challengetemple.externalapi.FabricInventoryApi")
        .withForge("com.holybuckets.challengetemple.externalapi.ForgeInventoryApi")
        .build();
        inventoryApi.setInstance(inventoryApi);

            EventRegistrar registrar = EventRegistrar.getInstance();
            TempleManager.init(registrar);
            ManagedChallenger.init(registrar);

            //register events
            registrar.registerOnLevelLoad(this::onLevelLoad);
            registrar.registerOnLevelUnload(this::onLevelUnload);
    }


    private static final ResourceLocation CHALLENGE_DIM = new ResourceLocation(Constants.MOD_ID, "challenge_dimension");
    private static final ResourceLocation OVERWORLD_DIM = new ResourceLocation("minecraft", "overworld");
    private void onLevelLoad(LevelLoadingEvent event)
    {
        Constants.LOG.info("Level loaded: {}", event.getLevel() );
        Level level = (Level) event.getLevel();
        if(level.isClientSide()) return;

        if( HBUtil.LevelUtil.testLevel(level, OVERWORLD_DIM )  ) {
            this.templeManager = new TempleManager( (ServerLevel) level, portalApi);
        } else if ( HBUtil.LevelUtil.testLevel(level, CHALLENGE_DIM ) ) {
            this.challengeDimension = (ServerLevel) level;
            TempleManager.setChallengeLevel( (ServerLevel) level);
            this.inventoryApi.setChallengeLevel((ServerLevel) level);
        }
    }

    private void onLevelUnload(LevelLoadingEvent.Unload event )
    {
        //Constants.LOG.info("Level unloaded: {}", event.getLevel() );
        Level level = (Level) event.getLevel();
        if( HBUtil.LevelUtil.testLevel(level, OVERWORLD_DIM )  ) {
            if( this.templeManager != null )
                this.templeManager.shutdown();
            this.templeManager = null;
        } else if ( HBUtil.LevelUtil.testLevel(level, CHALLENGE_DIM ) ) {
            int i = 0;
        }

    }


}
