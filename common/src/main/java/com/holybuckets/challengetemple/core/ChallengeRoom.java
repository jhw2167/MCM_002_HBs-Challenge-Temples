package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.ChallengeTempleMain;
import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.foundation.HBUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChallengeRoom {

    private static final String CLASS_ID = "007"; // Class ID for logging purposes

    private final String challengeId;
    private BlockEntity structureBlock;
    private StructureTemplate structureTemplate;

    //Statics
    static ServerLevel CHALLENGE_LEVEL;
    static final int CHALLENGE_DIM_HEIGHT = 64;

    //Offset for structureBlock that constructs the start challenge_room
    private static Vec3i STRUCTURE_BLOCK_OFFSET = new Vec3i(0, CHALLENGE_DIM_HEIGHT+1 , 0);
    private static Vec3i[] STRUCTURE_BLOCK_PIECE_OFFSETS = {
        new Vec3i(-1, -1, -1), // 00
        new Vec3i(32, 0, 0), // 01
        new Vec3i(0, 0, 32), // 02
        new Vec3i(32, 0, 32), // 03
        new Vec3i(0, 48, 0), // 04
        new Vec3i(32, 48, 0), // 05
        new Vec3i(0, 48, 32), // 06
        new Vec3i(32, 48, 32), // 07
        new Vec3i(0, 0, 0) // 08
    };

    ChallengeRoom(String challengeId)
    {
        this.challengeId = challengeId;
    }

    private static final String STRUCTURE_NAME = "challenge_room_4x4_";
    private boolean generateStructure(String id, boolean useTemplate) {
        
        StructureTemplateManager manager =  CHALLENGE_LEVEL.getStructureManager();
        ResourceLocation structure = new ResourceLocation(Constants.MOD_ID, STRUCTURE_NAME + id);
        StructureTemplate template = manager.getOrCreate(structure);
        Vec3i offset = STRUCTURE_BLOCK_PIECE_OFFSETS[(Integer.parseInt(id))];

        template.placeInWorld(
            CHALLENGE_LEVEL,                   // ServerLevel
            this.getWorldPos().offset(offset),                // Position to place at
            this.getWorldPos().offset(offset),                // ??
            (useTemplate) ? TEMPLATE_SETTINGS : REAL_SETTINGS, // Settings for placement
            CHALLENGE_LEVEL.getRandom(),
            (useTemplate) ? 2 : 18                         // Block update flag
        );

        return true;
    }

    //lsit ids from 01 to 08
    static final String[] ids = {"01", "02", "03", "04", "05", "06", "07", "08"};
    /**
     * Loads the physical structure in challenge_dimension by trigering all structure blocks
     * to generate.
     * @return true if strcuture was loaded successfully, false if any issues where encountered
     */
    public boolean loadStructure() {

        Arrays.stream(ids).forEach(id -> {
        Vec3i offset = STRUCTURE_BLOCK_PIECE_OFFSETS[(Integer.parseInt(id))];
        String msg = String.format("[%s] Loading structure with pos %s id: %s", this.challengeId, offset, id);
            LoggerProject.logDebug("007000", msg);
            this.generateStructure(id, false);
        });

        return true;
    }

    //* MIXIN
    public static void placeInWorld(
        ServerLevelAccessor world,
        BlockPos pos,
        BlockPos offset,
        StructurePlaceSettings settings,
        RandomSource random,
        int flags
    ) {
        System.out.println("=== StructureTemplate.placeInWorld called ===");
        System.out.println("World: " + world);
        System.out.println("Position: " + pos);
        System.out.println("Offset: " + offset);
        System.out.println("Settings: " + settings);
        System.out.println("Flags: " + flags);
    }


    //* UTILITY
    public BlockPos getWorldPos() {
        BlockPos pos = HBUtil.ChunkUtil.getWorldPos(challengeId);
        return new BlockPos(pos.getX(), CHALLENGE_DIM_HEIGHT, pos.getZ());
    }

    private static final StructurePlaceSettings TEMPLATE_SETTINGS = new StructurePlaceSettings()
        .setMirror(Mirror.NONE)
        .setRotation(Rotation.NONE)
        .setRotationPivot(BlockPos.ZERO)
        .setIgnoreEntities(false)
        .setBoundingBox(null)
        .setKeepLiquids(true)
        .setKnownShape(false)
        .setFinalizeEntities(false)
        .clearProcessors();

    private static final StructurePlaceSettings REAL_SETTINGS = new StructurePlaceSettings()
        .setMirror(Mirror.NONE)
        .setRotation(Rotation.NONE)
        .setRotationPivot(BlockPos.ZERO)
        .setIgnoreEntities(false)
        //.setBoundingBox(new BoundingBox(...))  // usually caller supplies this
        .setBoundingBox(null)  // usually caller supplies this
        .setKeepLiquids(false)  // usually false in worldgen
        .setKnownShape(true)
        .setFinalizeEntities(true)
        .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
        .addProcessor(JigsawReplacementProcessor.INSTANCE);



}
