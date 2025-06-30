package com.holybuckets.challengetemple.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.holybuckets.challengetemple.ChallengeTempleMain;
import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.challengetemple.externalapi.InventoryApi;
import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.datastore.DataStore;
import com.holybuckets.foundation.datastore.LevelSaveData;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.ServerTickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
    private boolean roomActive;
    private BlockEntity structureBlock;
    private StructureTemplate structureTemplate;
    private List<BlockPos> graveyardPositions = new ArrayList<>();


    //Statics
    static final int CHALLENGE_DIM_HEIGHT = 64;
    static final int MAX_GRAVES = 3;
    static final Map<String, ChallengeRoom> ACTIVE_ROOMS = new HashMap<>(); // Maps chunkId to ChallengeRoom
    static final Set<BlockPos> PROTECTED_GRAVE_POS = new HashSet<>(); // Set of grave positions that are protected

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

    //** CORE

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


    //A challenge could be a 2x2 or 4x4 room
    private static final String CHALLENGE_NAME = "challenge_<challengeId>_<pieceId>";
    private static final String STRUCTURE_NAME = "challenge_room_4x4_";
    private boolean generateStructure(String pieceId, boolean useTemplate)
    {
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



    /**
     * Triggers a refresh of the structure by reloading all parts
     * @return
     */
    boolean refreshStructure() {
        this.roomLoaded = false;
        return this.loadStructure();
    }


    public BlockPos getWorldPos() {
        BlockPos pos = HBUtil.ChunkUtil.getWorldPos(chunkId);
        return new BlockPos(pos.getX(), CHALLENGE_DIM_HEIGHT, pos.getZ());
    }

    public String getChallengeId() {
        return this.challengeId;
    }

    public void setActive(boolean isActive) {
        this.roomActive = isActive;
    }



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
        BlockPos pos = this.getWorldPos();

        //if (pos == null) //dont want to create some new grave with empty items to overwrite old one
        {
            // Calculate new position for the grave
            int z = this.graveyardPositions.size() % GRVYRD_MAX_Z;
            int x = ((this.graveyardPositions.size() / GRVYRD_MAX_Z) % GRVYRD_MAX_X)*-1; // -1 to start at -1
            if (z >= GRVYRD_MAX_Z) {
                z = 0; // Reset z if it exceeds max
                x--; // Move x back one step
            }

            pos = pos.offset(GRAVEYARD_START_OFFSET).offset(x, 0, z);
            if( PROTECTED_GRAVE_POS.contains(pos) ) {
                pos = pos.offset(0, 0, 1);
            }
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

    /**
     * Remove the protected grave position, the actual grave will be removed periodically ontick
     * @param pos
     */
    void removeGravePos(BlockPos pos) {
        PROTECTED_GRAVE_POS.remove(pos); // Remove from protected graves
    }

    //* UTILITY

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


    //** STATICS
    public static void init(EventRegistrar reg) {
        // Register the static event handler
        reg.registerOnServerTick(EventRegistrar.TickType.ON_120_TICKS, ChallengeRoom::on120TicksClearGraves);
    }

    public static void load() {
        DataStore ds = GeneralConfig.getInstance().getDataStore();
        LevelSaveData levelData = ds.getOrCreateLevelSaveData(Constants.MOD_ID, CHALLENGE_LEVEL);

        JsonElement data = levelData.get("challengeRoomData");
        if( data != null) {
            String positions = data.getAsJsonObject().get("gravePositions").getAsString();
            List<BlockPos> gravePos = HBUtil.BlockUtil.deserializeBlockPos(positions);
            PROTECTED_GRAVE_POS.addAll( gravePos );
        }


    }

    private static void on120TicksClearGraves(ServerTickEvent event)
    {
        // Skip clearing graves if any room is active
        if (ACTIVE_ROOMS.values().stream().anyMatch(room -> room.roomActive)) {
            return;
        }

        // Clear graves every 120 ticks
        int totalGraves = ACTIVE_ROOMS.values().stream()
            .mapToInt(room -> room.graveyardPositions.size())
            .sum();

        if (totalGraves > MAX_GRAVES) {
            MinecraftServer server = GeneralConfig.getInstance().getServer();
            ChallengeTempleMain.INSTANCE.inventoryApi.clearUnusedGraves(server);
        }
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


    /**
     * Clear static values
     * MUST STORE MOST RECENT GRAVE DATA TO AVOID IT CLEARING BETWEEN GAME RESTARTS
     */
    public static void shutdown()
    {
        //1. Clear unused graves
        MinecraftServer server = GeneralConfig.getInstance().getServer();
        //ChallengeTempleMain.INSTANCE.inventoryApi.clearUnusedGraves(server);

        //2. Save most recent grave for each player using HBDataStore
        InventoryApi api = ChallengeTempleMain.INSTANCE.inventoryApi;
        Map<ServerPlayer, BlockPos> graveData = api.getGravePos();

        String serializedPositions = HBUtil.BlockUtil.serializeBlockPos(graveData.values().stream().toList());
        JsonObject data = new JsonObject();
        data.addProperty("gravePositions", serializedPositions);

        DataStore dataStore = GeneralConfig.getInstance().getDataStore();
        LevelSaveData levelData = dataStore.getOrCreateLevelSaveData(Constants.MOD_ID, CHALLENGE_LEVEL);
        levelData.addProperty("challengeRoomData", data);

        //3. clear values
        for (ChallengeRoom room : ACTIVE_ROOMS.values()) {
            room.graveyardPositions.clear();
        }
        ACTIVE_ROOMS.clear();
    }

}
