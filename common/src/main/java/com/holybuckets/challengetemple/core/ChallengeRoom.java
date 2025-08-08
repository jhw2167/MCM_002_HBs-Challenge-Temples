package com.holybuckets.challengetemple.core;

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
import com.holybuckets.foundation.event.custom.DatastoreSaveEvent;
import com.holybuckets.foundation.event.custom.ServerTickEvent;
import com.holybuckets.foundation.event.custom.TickType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static com.holybuckets.challengetemple.ChallengeTempleMain.DEV_MODE;
import static com.holybuckets.challengetemple.core.TempleManager.*;
import static com.holybuckets.challengetemple.core.ManagedTemple.*;
import static com.holybuckets.challengetemple.core.ChallengeDB.ChallengeFilter;

public class ChallengeRoom {

    private static final String CLASS_ID = "007"; // Class ID for logging purposes

    private String challengeId;
    private String chunkId;
    private Challenge challenge;
    private final Vec3i overworldExitPos;
    private final Level returnLevel;
    private boolean roomLoaded;
    private boolean roomActive;
    private boolean roomCompleted;
    private BlockEntity structureBlock;
    private StructureTemplate structureTemplate;
    private List<BlockPos> graveyardPositions = new ArrayList<>();
    Set<String> forceloadedChunks = new HashSet<>();

    //challenge exit
    private BlockPos worldPos;
    private BlockPos challengeExitPos;
    private BlockPos exitStructurePos;
    private Entity exitPortal;      //exit portal on the floor, 2x2 in x,z direction
    private ChallengeKeyBlockManager challengeKeyBlocks;


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
    static BlockState EXIT_PORTAL_BLOCK;

    ChallengeRoom(String chunkId, Vec3i overworldExitPos, Level returnLevel, String challengeId)
    {
        if(chunkId.equals( SPECIAL_TEMPLE)) {
            int i = 0;
        }
        this.chunkId = chunkId;
        this.worldPos = ChallengeRoom.getWorldPos(chunkId);
        this.overworldExitPos = overworldExitPos;
        this.returnLevel = returnLevel;
        this.setChallenge(challengeId);
        this.roomLoaded = false;
        this.roomActive = false;
        this.roomCompleted = false;


        //Forceload all chunks in range - use nested  for loop over x and z axis starting with chunkId
        int chunkXStart = HBUtil.ChunkUtil.getChunkPos(chunkId).x;
        int chunkZStart = HBUtil.ChunkUtil.getChunkPos(chunkId).z;
        Vec3i size = this.challenge.getSize();
        for(int x = chunkXStart; x < chunkXStart + size.getX(); x++) {
            for(int z = chunkZStart; z < chunkZStart + size.getZ(); z++) {
                String id = HBUtil.ChunkUtil.getId(x, z);
                if(!HBUtil.ChunkUtil.isChunkForceLoaded(CHALLENGE_LEVEL, id)) {
                    forceloadedChunks.add(id);
                    HBUtil.ChunkUtil.forceLoadChunk(CHALLENGE_LEVEL, id);
                }
            }
        }

        ACTIVE_ROOMS.put(chunkId, this); // Register this room in the static map

    }


    ChallengeRoom(String chunkId, Vec3i overworldExitPos, Level returnLevel)
    {
        this(chunkId, overworldExitPos, returnLevel, null);
    }


    //** GETTERS
    BlockPos getWorldPos() {
        return this.worldPos;
    }

    boolean isRoomCompleted() {
        return this.roomCompleted;
    }

    public void startChallenge() {
        if(this.challengeKeyBlocks != null)
            this.challengeKeyBlocks.refreshBlocks();
        this.generateExitStructure();
        this.roomActive = true;
    }

    public List<ItemStack> getChallengeLoot() {
        return this.challenge.getLootRules().getSpecificLoot();
    }

    public String getChallengeId() {
        return this.challengeId;
    }

    public Challenge getChallenge() { return challenge; }

    private void setActive(boolean isActive) {
        this.roomActive = isActive;
    }

    public void setChallenge(String ChallengeId)
    {
        ChallengeFilter filter = new ChallengeFilter()
            .setChallengeId(ChallengeId);
        this.challenge = ChallengeDB.chooseChallenge(filter);
        if(this.challenge == null)
            this.challenge = ChallengeDB.chooseChallenge(null);

        this.challengeId = this.challenge.getChallengeId();
    }

    //** CORE


    /**
     * Loads the physical structure in challenge_dimension by triggering all structure blocks
     * to generate.
     * @return true if structure was loaded successfully, false if any issues where encountered
     */
    boolean loadStructure()
    {
        if( DEV_MODE ) return  true;
        if( this.roomLoaded ) return false;

        this.roomLoaded = true;
        int totalPieces = challenge.getTotalPieces();
        boolean succeeded = true;
        for(int i = 0; i < totalPieces; i++) {
            String id = String.format("%02d", i);
            succeeded = true && this.generateStructure(id, false);
        }

        if(succeeded)
        {
            if(this.challengeKeyBlocks == null)
                this.challengeKeyBlocks = new ChallengeKeyBlockManager( this.worldPos, challenge.getSize());
        }

        return succeeded;
    }


    //A challenge could be a 2x2 or 4x4 room
    private static final String CHALLENGE_NAME = "challenge_<challengeId>_<pieceId>";
    private static final String STRUCTURE_NAME = "challenge_room_4x4_";

    /**
     * Generates the individual structure pieces
     * @param pieceId
     * @param useTemplate
     * @return
     */
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

       boolean succeeded = template.placeInWorld(
            CHALLENGE_LEVEL,                   // ServerLevel
            this.getWorldPos().offset(offset),                // Position to place at
            this.getWorldPos().offset(offset),                // ??
            (useTemplate) ? TEMPLATE_SETTINGS : REAL_SETTINGS, // Settings for placement
            CHALLENGE_LEVEL.getRandom(),
            (useTemplate) ? 2 : 18                         // Block update flag
        );

        //Need to replace structure blocks with actual blocks
        List<Block> repl = challenge.getReplaceEntityBlocks();
        if( pieceId.equals( "03" ) ) {
            BlockState replaceEntityBlock = repl.get(0).defaultBlockState();
            CHALLENGE_LEVEL.setBlock( getWorldPos().offset(offset), replaceEntityBlock, 18 );
        } else if( pieceId.equals( "07" ) ) {
            BlockState replaceEntityBlock = repl.get(1).defaultBlockState();
            CHALLENGE_LEVEL.setBlock( getWorldPos().offset(offset), replaceEntityBlock, 18 );
        }

        return succeeded;
    }



    /**
     * 1. Parses entire structure for soul torches in a 6x6 area, using size from Challenge Obj
     * 2. Builds exist structure in bottom left corner (min x, min z)
     * 3. parses for soul torches in interior 4x4 area
     * 4. builds exit portal if all soul torches are found
     * @return
     */
    private boolean generateExitStructure()
    {

        if( this.exitPortal != null ) {
            PORTAL_API.removePortal(this.exitPortal);
            this.exitPortal = null;
        }

        if( this.exitStructurePos == null)
            this.exitStructurePos = challengeKeyBlocks.getExitStructurePos();

        //log error and return false if not found
        if( this.exitStructurePos == null ) {
            LoggerProject.logError("007005", "Exit portal block not found in challenge room at " + this.getWorldPos());
            return false;
        }

        //1. Load the challenge_exit structure
        ResourceLocation exitStructure = new ResourceLocation(Constants.MOD_ID, "challenge_exit_00");
        StructureTemplateManager manager = CHALLENGE_LEVEL.getStructureManager();
        Optional<StructureTemplate> temp = manager.get(exitStructure);
        if(!temp.isPresent()) {
            LoggerProject.logError(CLASS_ID, "Failed to load challenge exit structure: " + exitStructure);
            return false;
        }

        //Place at exitPortalPos
        this.structureTemplate = temp.get();
        this.structureTemplate.placeInWorld(
            CHALLENGE_LEVEL,                   // ServerLevel
            this.exitStructurePos,                // Position to place at
            this.exitStructurePos,                // ??
            REAL_SETTINGS,                      // Settings for placement
            CHALLENGE_LEVEL.getRandom(),
            18                                  // Block update flag
        );

        this.roomLoaded = true;

        //2. Generate the exit portal
        //return this.generateExitPortal(); triggered by player in managedChallenger
        return true;
    }

        private Vec3 toVec3(Vec3i pos) {
            return new Vec3(pos.getX(), pos.getY(), pos.getZ());
        }


        private static final Vec3i EXIT_PORTAL_MARKER_OFFSET = new Vec3i(1, 1, 1);
        /**
         * Tries to test if the room is complete by determining
         * if all soul torches are present in the structure
         * @return true if the challenge is completed
         */
        boolean testRoomCompleted()
        {
            if(this.exitStructurePos == null) return false; // No exit structure found

              BlockPos portalTorchPos = this.exitStructurePos.offset(EXIT_PORTAL_MARKER_OFFSET);
            BlockPos temp = portalTorchPos;
            //** Check all 4 soul torches in the 4x4 area
            BlockState state = CHALLENGE_LEVEL.getBlockState(temp);
            for(int i =0; i<4; i++)
            {
                if(!state.equals(EXIT_PORTAL_BLOCK))  return false;
                if(i == 0) temp = temp.offset(3, 0, 0); // Move to next torch
                else if(i == 1) temp = temp.offset(0, 0, 3); // Move to next torch
                else if(i == 2) temp = temp.offset(-3, 0, 0); // Move to next torch
                state = CHALLENGE_LEVEL.getBlockState(temp);
            }

            this.roomCompleted = true;
            return  generateExitPortal(portalTorchPos);
            //return true;
        }


        private static final Vec3i EXIT_PORTAL_OFFSET = new Vec3i(2, -1, 2);
        private boolean generateExitPortal(BlockPos portalTorchPos)
        {
            if( this.exitPortal != null ) {
                PORTAL_API.removePortal(this.exitPortal);
                this.exitPortal = null;
            }

            Vec3i portalPos = portalTorchPos.offset(EXIT_PORTAL_OFFSET);

            this.exitPortal = PORTAL_API.createPortal(
                P_WIDTH, P_HEIGHT,
                CHALLENGE_LEVEL,
                returnLevel,
                toVec3( portalPos ),
                toVec3( overworldExitPos),
                Direction.UP
            );

            return this.exitPortal != null;
        }

    /**
     * Triggers a refresh of the structure by reloading all parts
     * @return
     */
    boolean refreshStructure() {
        this.roomLoaded = false;
        if(this.exitPortal != null) {
            PORTAL_API.removePortal(this.exitPortal);
            this.exitPortal = null;
        }

        this.challengeKeyBlocks.clearEntities();
        if( this.loadStructure() ) {
            this.challengeKeyBlocks.refreshBlocks();
            return this.generateExitStructure();
        }

        return false;
    }


    //** CORE CHALLENGER

    /**
     * Triggers processing after challenger dies during the challenge.
     * @param c
     *
     */
    public void onChallengerDeath(ManagedChallenger c) {
        this.refreshStructure();
    }

    /**
     * isChallengerFailed checks if the challenger has failed challenge
     * either by total deaths or some other method
     * @param c
     * @return true if challenger has failed, false otherwise
     */
    public boolean isChallengerFailed(ManagedChallenger c) {
        return false;
    }

    //* UTILITY

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

    private void clearExitPortal() {
        if (this.exitPortal != null) {
            PORTAL_API.removePortal(this.exitPortal);
            this.exitPortal = null;
        }
        this.exitStructurePos = null;
        this.roomLoaded = false;
    }

    void roomShutdown()
    {
        clearExitPortal();
        if(this.challengeKeyBlocks != null)
            this.challengeKeyBlocks.clearPortals();

        // Clear forceloaded chunks
        for (String chunkId : forceloadedChunks) {
        if( HBUtil.ChunkUtil.isChunkForceLoaded(CHALLENGE_LEVEL, chunkId))
            HBUtil.ChunkUtil.unforceLoadChunk(CHALLENGE_LEVEL, chunkId);
        }
        setActive(false);
    }

    //** STATICS

    public static void init(EventRegistrar reg) {
        // Register the static event handler
        reg.registerOnServerTick(TickType.ON_120_TICKS, ChallengeRoom::on120TicksClearGraves);
        reg.registerOnServerTick(TickType.ON_20_TICKS, ChallengeRoom::on20TicksTryExitPortal);
        reg.registerOnDataSave(ChallengeRoom::onDataSaveEvent);

        PORTAL_API = ChallengeTempleMain.INSTANCE.portalApi;
    }

    /**
     * Loads after TempletManager loads
     */
    public static void load()
    {
        DataStore ds = GeneralConfig.getInstance().getDataStore();
        LevelSaveData levelData = ds.getOrCreateLevelSaveData(Constants.MOD_ID, CHALLENGE_LEVEL);

        JsonElement data = levelData.get("challengeRoomData");
        if( data != null) {
            String positions = data.getAsJsonObject().get("gravePositions").getAsString();
            List<BlockPos> gravePos = HBUtil.BlockUtil.deserializeBlockPos(positions);
            PROTECTED_GRAVE_POS.addAll( gravePos );
        }

        EXIT_PORTAL_BLOCK = HBUtil.BlockUtil
            .blockNameToBlock("minecraft", "soul_torch")
            .defaultBlockState();

    }

    public static BlockPos getWorldPos(String chunkId) {
        BlockPos pos = HBUtil.ChunkUtil.getWorldPos(chunkId);
        return new BlockPos(pos.getX(), CHALLENGE_DIM_HEIGHT, pos.getZ());
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

    private static void on20TicksTryExitPortal(ServerTickEvent event) {
        ACTIVE_ROOMS.values().stream()
            .filter(room -> room.roomActive && room.exitPortal == null)
            .forEach(r -> r.testRoomCompleted());
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


    public static void onDataSaveEvent(DatastoreSaveEvent e)
    {
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
    }

    /**
     * Clear static values
     * MUST STORE MOST RECENT GRAVE DATA TO AVOID IT CLEARING BETWEEN GAME RESTARTS
     */
    public static void shutdown()
    {
        //1. Clear unused graves


        //3. clear values
        for (ChallengeRoom room : ACTIVE_ROOMS.values()) {
            room.graveyardPositions.clear();
            room.clearExitPortal();
        }
        ACTIVE_ROOMS.clear();

        //4. Clear portal

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
}
