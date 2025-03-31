package com.holybuckets.challengetemple;

import com.holybuckets.challengetemple.structure.GridStructurePlacement;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.GenerationStep;

public class ChallengeTempleMain {

    public static final String CLASS_ID = "001";    //unused variable, value will be used for logging messages

    // Define mod id in a common place for everything to reference
    public static final String MODID = Constants.MOD_ID;
    public static final String NAME = "HBs Challenge Temples";
    public static final String VERSION = "1.0.0f";
    public static final Boolean DEBUG = false;

    public static ChallengeTempleMain INSTANCE;

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

    }

}
