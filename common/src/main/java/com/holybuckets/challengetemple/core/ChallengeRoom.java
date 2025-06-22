package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.ChallengeTempleMain;
import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.ServerTickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.antlr.v4.runtime.misc.MultiMap;

import java.util.*;

import static com.holybuckets.challengetemple.core.TempleManager.CHALLENGE_LEVEL;

public class ChallengeRoom {

    private static final String CLASS_ID = "007"; // Class ID for logging purposes

    private final String challengeId;
    private final String chunkId;
    private boolean roomLoaded;
    private BlockEntity structureBlock;
    private StructureTemplate structureTemplate;
    private List<BlockPos> graveyardPositions = new ArrayList<>(); // Maps player UUID to their grave position

    //Statics
    static final int CHALLENGE_DIM_HEIGHT = 64;
    static final int MAX_GRAVES = 3;
    static final Map<String, ChallengeRoom> ACTIVE_ROOMS = new HashMap<>(); // Maps chunkId to ChallengeRoom

    //Offset for structureBlock that constructs the start challenge_room
    private static Vec3i STRUCTURE_BLOCK_OFFSET = new Vec3i(0, CHALLENGE_DIM_HEIGHT+1 , 0);
    private static Vec3i[] STRUCTURE_BLOCK_PIECE_OFFSETS = {
        new Vec3i(0, 0, 0), // 00
        new Vec3i(32, 0, 0), // 01
        new Vec3i(0, 0, 32), // 02
        new Vec3i(32, 0, 32), // 03
        new Vec3i(0, 48, 0), // 04
        new Vec3i(32, 48, 0), // 05
        new Vec3i(0, 48, 32), // 06
        new Vec3i(32, 48, 32), // 07
        new Vec3i(0, 0, 0) // 08
    };

    ChallengeRoom(String chunkId)
    {
        this.chunkId = chunkId;
        this.challengeId = Challenges.chooseChallengeId(null);
        this.roomLoaded = false;
        ACTIVE_ROOMS.put(chunkId, this); // Register this room in the static map
    }


    //A challenge could be a 2x2 or 4x4 room
    private static final String CHALLENGE_NAME = "challenge_<challengeId>_<pieceId>";
    private static final String STRUCTURE_NAME = "challenge_room_4x4_";
    private boolean generateStructure(String pieceId, boolean useTemplate) {
        
        StructureTemplateManager manager =  CHALLENGE_LEVEL.getStructureManager();
        String location = CHALLENGE_NAME.replace("<challengeId>", challengeId).replace("<pieceId>", pieceId);
        //String location = STRUCTURE_NAME + pieceId;
        ResourceLocation structure = new ResourceLocation(Constants.MOD_ID, location);
        Optional<StructureTemplate> temp = manager.get(structure);
        if(!temp.isPresent()) return false;

        StructureTemplate template = temp.get();
        Vec3i offset = STRUCTURE_BLOCK_PIECE_OFFSETS[(Integer.parseInt(pieceId))];

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
    static final String[] ids = {"00", "01", "02", "03", "04", "05", "06", "07", "08"};
    /**
     * Loads the physical structure in challenge_dimension by trigering all structure blocks
     * to generate.
     * @return true if strcuture was loaded successfully, false if any issues where encountered
     */
    boolean loadStructure()
    {
        if( this.roomLoaded ) return false;

        this.roomLoaded = true;
        Arrays.stream(ids).forEach(id -> {
        Vec3i offset = STRUCTURE_BLOCK_PIECE_OFFSETS[(Integer.parseInt(id))];
        String msg = String.format("[%s] Loading structure with pos %s id: %s", this.chunkId, offset, id);
            LoggerProject.logDebug("007000", msg);
            this.generateStructure(id, false);
        });

        return true;
    }

    /**
     * Triggers a refresh of the structure by reloading all parts
     * @return
     */
    boolean refreshStructure() {
        this.roomLoaded = false;
        return this.loadStructure();
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
        BlockPos pos = HBUtil.ChunkUtil.getWorldPos(chunkId);
        return new BlockPos(pos.getX(), CHALLENGE_DIM_HEIGHT, pos.getZ());
    }

    public String getChallengeId() {
        return this.challengeId;
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


    private static final Vec3i GRAVEYARD_START_OFFSET = new Vec3i(-4, 1, 1);
    private static final int GRVYRD_MAX_Z = 64; // max z position for graveyard, resets at 0
    private static final int GRVYRD_MAX_X = 16; // max x position for graveyard, resets at -1
    /**
     * Adds a player grave to the "graveyar" in the challenge dimension where challenger
     * items will be stored untl challenge is complete. moves in positive z axis until z > 64
     * then resets at 0, x moves -1
     * @param player
     */
    public BlockPos addGrave(ManagedChallenger player)
    {
        //setupt a while loop to wait for player to be in CHALLENGE_DIM
        UUID playerId = player.getPlayer().getUUID();
        BlockPos pos;

        //if (pos == null) //dont want to create some new grave with empty items to overwrite old one
        {
            // Calculate new position for the grave
            int z = this.graveyardPositions.size() / GRVYRD_MAX_Z;
            int x = ((this.graveyardPositions.size() / GRVYRD_MAX_Z) % GRVYRD_MAX_X)*-1; // -1 to start at -1
            if (z >= GRVYRD_MAX_Z) {
                z = 0; // Reset z if it exceeds max
                x--; // Move x back one step
            }
            pos = this.getWorldPos().offset(GRAVEYARD_START_OFFSET).offset(x, 0, z);
            this.graveyardPositions.add(pos);
        }

        ChallengeTempleMain.INSTANCE.inventoryApi.createGrave(
            player.getServerPlayer(),
            pos
        );
        /*
        ChallengeTempleMain.INSTANCE.inventoryApi.clearInventory(
            managedChallenger.getServerPlayer()
        );
        */
        return pos;
    }


    //** STATICS
    public static void init(EventRegistrar reg) {
        // Register the static event handler
        reg.registerOnServerTick(EventRegistrar.TickType.ON_120_TICKS, ChallengeRoom::on120TicksClearGraves);
    }

    private static void on120TicksClearGraves(ServerTickEvent event) {
        // Clear graves every 120 ticks
        int totalGraves = ACTIVE_ROOMS.values().stream()
            .mapToInt(room -> room.graveyardPositions.size())
            .sum();

        if (totalGraves > MAX_GRAVES) {
            MinecraftServer server = GeneralConfig.getInstance().getServer();
            ChallengeTempleMain.INSTANCE.inventoryApi.clearUnusedGraves(server);
        }
    }



}
