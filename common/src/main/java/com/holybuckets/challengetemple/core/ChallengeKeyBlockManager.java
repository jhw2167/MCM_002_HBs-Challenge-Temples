package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.challengetemple.block.be.ChallengeSingleUseChestBlockEntity;
import com.holybuckets.foundation.event.EventRegistrar;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.WEST;
import static com.holybuckets.challengetemple.core.TempleManager.CHALLENGE_LEVEL;
import static com.holybuckets.challengetemple.core.TempleManager.PORTAL_API;
import static com.holybuckets.foundation.HBUtil.BlockUtil;


/**
 * Some blocks in a challenge have special properties that require
 * programatic handling, we will handle them here.
 */
public class ChallengeKeyBlockManager {

    public static final String CLASS_ID = "027";


    private final ServerLevel level;
    final BlockPos startPos;
    final Vec3i size;
    boolean loaded;

    private final Map<Block, List<BlockPos>> challengeBlocks;
    private final Map<BlockState, List<BlockPos>> blockStateReplacements;
    private final List<Entity> portals;
    private final Set<BlockPos> usedChests;
    private final Challenge challenge;


    /**
     * Creates and load array of key blocks in the challenge area.
     *
     * @param startPos
     * @param size
     */
    public ChallengeKeyBlockManager(BlockPos startPos, Vec3i size, Challenge challenge)
    {
        this.challengeBlocks = new HashMap<>();
        this.blockStateReplacements = new HashMap<>();
        this.portals = new ArrayList<>();
        this.level = CHALLENGE_LEVEL;
        this.startPos = startPos;
        this.size = size;
        this.loaded = false;

        this.usedChests = new HashSet<>();

        initReplacers();
        loadChallengeBlocks();
        refreshBlocks();
    }

    private void initReplacers() {
        //Initialize replacers
        blockStateReplacements.put(Blocks.AIR.defaultBlockState(), new LinkedList<>());
        blockStateReplacements.put(ModBlocks.challengeBrick.defaultBlockState(), new LinkedList<>());
    }


    //** CORE

    /**
     * Finds all KEY_BLOCKS (wool, pistons, redstone, soul torches) in the challenge
     * and stores them in HashTable for individual processing of dynamic blocks.
     */
    private void loadChallengeBlocks()
    {
        //Load all key blocks
        for (Block block : KEY_BLOCKS) {
            challengeBlocks.put(block, new LinkedList<>());
        }

        //1. Parse area for minecraft:soul_torch
        Vec3i sz = size;
        if( loaded )
            sz = new Vec3i(0,0,0);

        for(int x = 0; x < sz.getX(); x++) {
            for(int y = 0; y < sz.getY(); y++) {
                for (int z = 0; z < sz.getZ(); z++) {
                    BlockPos pos = startPos.offset(x, y, z);
                    BlockState state = CHALLENGE_LEVEL.getBlockState(pos);
                    Block block = state.getBlock();
                    if (KEY_BLOCKS.contains(state.getBlock())) {
                        challengeBlocks.get(block).add(pos);
                    }
                }
            }
        }

    }

    public void refreshBlocks()
    {
        //Clear portals, generate portals, replace blocks
        clearPortals();
        clearEntities();

        generatePortals();
        spawnEntities();

        setRandomBricks();
        checkChests();

        replaceBlocks();
        resetRedstone();

    }


    private void setRandomBricks() {
        List<BlockPos> randomBricks = getPositions(ModBlocks.randomBrick);
        if (randomBricks.isEmpty()) return;

        for (BlockPos pos : randomBricks) {
            boolean spawn = level.getRandom().nextFloat() < challenge.randomBrickSpawnChance;
            BlockState replacement = spawn ? ModBlocks.challengeBrick.defaultBlockState() : Blocks.AIR.defaultBlockState();
            blockStateReplacements.get(replacement).add(pos);
        }
    }

    private void checkChests()
    {
        //** SINGLE USE CHESTS -- Clear inventory
        for (BlockPos pos : getPositions(ModBlocks.challengeSingleUseChest)) {
            if (usedChests.contains(pos)) {
                BlockEntity entity = CHALLENGE_LEVEL.getBlockEntity(pos);
                if (entity instanceof ChallengeSingleUseChestBlockEntity) {
                    ChallengeSingleUseChestBlockEntity chest = (ChallengeSingleUseChestBlockEntity) entity;
                    int i=0;
                    while(i < chest.getContainerSize()) {
                        chest.setItem(i++, ItemStack.EMPTY);
                    }
                }
            }
        }

    }


    void clearEntities()
    {
        // Clear tracked portals
        clearPortals();

        // Create bounding box for entire challenge area
        AABB aabb = new AABB(
            startPos.getX()+1, startPos.getY(), startPos.getZ()+1,
            startPos.getX() + size.getX(), 
            startPos.getY() + size.getY(),
            startPos.getZ() + size.getZ()
        );

        // Get all entities in the area
        List<Entity> entities = CHALLENGE_LEVEL.getEntities((Entity)null, aabb, 
            (entity) -> !(entity instanceof ServerPlayer));

        // Remove entities
        for (Entity entity : entities) {
            if (PORTAL_API.isPortal(entity)) {
                PORTAL_API.removePortal(entity);
            }
            else {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }

    }

    public void clearPortals() {
        portals.forEach(PORTAL_API::removePortal);
        portals.clear();
    }


    private void replaceBlocks() {
        //Replace blocks with air or brick
        for (Map.Entry<BlockState, List<BlockPos>> entry : blockStateReplacements.entrySet()) {
            BlockState state = entry.getKey();
            List<BlockPos> positions = entry.getValue();
            for (BlockPos pos : positions) {
                CHALLENGE_LEVEL.setBlockAndUpdate(pos, state);
            }
        }
    }


    private void generatePortals()
    {
        List<Block> woolBlocks = new ArrayList<>();
        for (Block block : challengeBlocks.keySet())
        {
            if( challengeBlocks.get(block).isEmpty() ) continue;
            String name = BlockUtil.blockToString(block);
            if( name.endsWith("_wool") ) woolBlocks.add(block);
        }

        for( Block wool : woolBlocks )
        {
            List<BlockPos> pistons = challengeBlocks.getOrDefault(Blocks.PISTON, Collections.emptyList());
            List<BlockPos> stickyPistons = challengeBlocks.getOrDefault(Blocks.STICKY_PISTON, Collections.emptyList());

            if( pistons.isEmpty() || stickyPistons.isEmpty() ) continue;

            Map<BlockPos, Pair<Integer, Direction>> sourcePortals = new HashMap<>();
            for( BlockPos woolPos : challengeBlocks.get(wool) ) {
                Pair<Integer, Direction> pistonInfo = findPistonsAboveBelow(woolPos, Blocks.STICKY_PISTON);
                if( pistonInfo == null ) continue; // no pistons above or below
                sourcePortals.put(woolPos, pistonInfo);
            }

            if( sourcePortals.isEmpty() ) continue; // no wool blocks with pistons above or below

            Map<BlockPos, Pair<Integer, Direction>> destPortals = new HashMap<>();
            for( BlockPos woolPos : challengeBlocks.get(wool) ) {
                Pair<Integer, Direction> pistonInfo = findPistonsAboveBelow(woolPos, Blocks.PISTON);
                if( pistonInfo == null ) continue; // no pistons above or below
                destPortals.put(woolPos, pistonInfo);
            }

            //Determine the plane the sourcePortals are in
            int xWidth = 0, zWidth = 0;
            //find the max difference in the x dimension and the z dimension using a nested for loop
            BlockPos[] positions = sourcePortals.keySet().toArray(new BlockPos[0]);
            BlockPos topLeftSource = positions[0];
            for (int i = 0; i < positions.length; i++) {
                if( positions[i].getX() > topLeftSource.getX() || positions[i].getZ() > topLeftSource.getZ() )
                    topLeftSource = positions[i];

                for (int j = i + 1; j < positions.length; j++) {
                    BlockPos a = positions[i];
                    BlockPos b = positions[j];

                    int tempX = Math.abs(a.getX() - b.getX());
                    int tempZ = Math.abs(a.getZ() - b.getZ());

                    if (tempX > xWidth) xWidth = tempX;
                    if (tempZ > zWidth) zWidth = tempZ;
                }
            }

            int height = 0;
            int width = 0;
            Vec3 centering = Vec3.ZERO;
            Vec3 destCentering = Vec3.ZERO;
            Direction  dir = sourcePortals.get(positions[0]).getRight();
            /*
             *  Consider:
             *  - SOUTH = +z
             *  - NORTH = -z
             *  - EAST = +x
             *  - WEST = -x
             *  - UP = +y
             *  - DOWN = -y
             *
             * if portal is in Z dimension, move to the center of the bock in direction portal is facing
             * if portal is in X dimension, move to the center of the block in direction portal is facing +/- (0, 0, 0.5)
             * if portal is in Y dimension, move to the center of the block in direction portal is facing +/- (, 0.5, )
             *
             * if portal width is ODD, move have block in dimension ALIGNED to the width, to keep portal centered on block
             * if portal width is EVEN, portal centers on block edges already
             */
            if( dir == EAST || dir == WEST ) {  //portal in z dimension
                width = zWidth+1;
                height = Math.abs( sourcePortals.get(positions[0]).getLeft() )-1;

              double normalCenter = (dir == EAST) ? 0.5 : -0.5;
              double blockAlignedCenter = (width % 2 == 0) ? 0 : 0.5;
              centering = new Vec3(normalCenter, 0,  blockAlignedCenter);
              destCentering = centering.multiply(-1, 1, 1); //dest portal is opposite direction in z dimension
            }
            else if( dir == NORTH || dir == SOUTH ) {  //portal in x dimension
                width = xWidth+1;
                height = Math.abs( sourcePortals.get(positions[0]).getLeft() )-1;

                double normalCenter = (dir == SOUTH) ? 0.5 : -0.5; //portal runs along x dimension, move to center of block in z dimension
                double blockAlignedCenter = (width % 2 == 0) ? 0 : 0.5;
                centering = new Vec3(blockAlignedCenter, 0, normalCenter);
                destCentering = centering.multiply(1, 1, -1);
            }
            else {  //portal is up or down
               height = xWidth+1;
               width = zWidth+1;
               dir = sourcePortals.get(positions[0]).getRight();

               centering = new Vec3(0, (height % 2 == 0) ? 0 : 0.5, 0);
                destCentering = centering.multiply(1, -1, 1);
            }

            //Portal starts in top left, most negative x, most positive z, highest y
            positions = destPortals.keySet().toArray(new BlockPos[0]);
            BlockPos topLeftDest = positions[0];
            for (int i = 0; i < positions.length; i++) {
                if( positions[i].getX() > topLeftDest.getX() || positions[i].getZ() > topLeftDest.getZ() )
                    topLeftDest = positions[i];
            }

            Direction destDir = destPortals.get(positions[0]).getRight();
            if( destDir == dir )
                destCentering = centering;

            //Create the portal entity
            Vec3 source = BlockUtil.toVec3( topLeftSource.offset(0, height, 0) );
            Vec3 dest = BlockUtil.toVec3( topLeftDest.offset(0, height, 0) );
            //Center the portal on the block in the direction the portal faces
            Entity portal = PORTAL_API.createPortal(width, height,
            CHALLENGE_LEVEL, CHALLENGE_LEVEL,
                source.add(centering), dest.add(destCentering),
             dir );

            portals.add(portal);

            //Cleanup blocks
            for (BlockPos pos : sourcePortals.keySet()) {
                cleanupBlocks(pos, sourcePortals.get(pos).getLeft(), Blocks.PISTON);
            }

            for (BlockPos pos : destPortals.keySet()) {
                cleanupBlocks(pos, destPortals.get(pos).getLeft(), Blocks.STICKY_PISTON);
            }

        }

    }

    private void cleanupBlocks(BlockPos pos, Integer total, Block type) {
        //Add wool block as air or brick
        if( total < 0) blockStateReplacements.get(AIR).add(pos);
        else blockStateReplacements.get(BRICK).add(pos);

        //add piston pos as air
        int d = (total > 0) ? 1 : -1;
        BlockPos temp = pos.offset(0, d, 0);
        for(int i = 0; i < Math.abs(total)-1; i++) {
            blockStateReplacements.get(R.get(type)).add(temp);
            temp = temp.offset(0, d, 0);
        }
    }


    private Pair<Integer, Direction> findPistonsAboveBelow(BlockPos woolPos, Block type)
    {
        BlockState up = level.getBlockState(woolPos.above());
        BlockState down = level.getBlockState(woolPos.below());

        int dir = 0;
        if(up.is(type)) {
            dir = 1; // up
        }
        else if(down.is(type)) {
            dir = -1; // down
        }
        else {
            return null; // no pistons above or below
        }

        BlockPos pos = woolPos.offset(0, dir, 0);
        BlockState next = level.getBlockState(pos);
        BlockState first = level.getBlockState(pos);
        while( next.is(type) ) {
            pos = pos.offset(0, dir, 0);
            next = level.getBlockState(pos);
        }

        return Pair.of((pos.getY() - woolPos.getY()), first.getValue(BlockStateProperties.FACING) );
    }


    /**
     * updateNeighbors for all redstone type blocks
     */
    private void resetRedstone() {

        //For all redstone blocks, place the block again where it is, update all neighbors of bloc below
        for (Block block : REDSTONE_BLOCKS)
        {
            List<BlockPos> positions = challengeBlocks.getOrDefault(block, Collections.emptyList());
            for (BlockPos pos : positions) {
                BlockState state = level.getBlockState(pos);
                level.setBlockAndUpdate(pos, state); // Replaces the block with itself
                BlockPos below = pos.below();
                if (level.getBlockState(below).isRedstoneConductor(level, below)) {
                    level.updateNeighborsAt(below, block);
                }
            }
        }
    }

    /**
     * Spawn entities programatically on specific blocks.
     * SkeletonBrick - skeleton
     * ZombieBrick - zombie
     *
     */
    private void spawnEntities() {

        //Spawn skeletons on skeleton bricks
        for (BlockPos pos : getPositions(ModBlocks.skeletonBrick) ) {
            Entity entity = EntityType.SKELETON.spawn(CHALLENGE_LEVEL, pos.above(), MobSpawnType.NATURAL);
            blockStateReplacements.get(R.get(ModBlocks.skeletonBrick)).add(pos);
        }

        for (BlockPos pos : getPositions(ModBlocks.zombieBrick) ) {
            Entity entity = EntityType.ZOMBIE.spawn(CHALLENGE_LEVEL, pos, MobSpawnType.NATURAL);
        }


    }


    public void onRoomShutdown() {
        this.clearPortals();
        this.clearEntities();
        this.usedChests.clear();
    }


    //** UTILITY

    public List<BlockPos> getPositions(Block b) {
        return challengeBlocks.getOrDefault(b, Collections.emptyList()).stream().toList();
    }

    public void challengerUsedBlock(BlockPos pos)
    {
        //Only scenario we care about currently is if its a single use chest
        BlockEntity entity = CHALLENGE_LEVEL.getBlockEntity(pos);
        if( entity == null) {
            //nothing
        } else if( entity instanceof ChallengeSingleUseChestBlockEntity) {
            usedChests.add(pos);
        }

    }

    /**
     * Going to have to block state update blocks and pistons!!
     */

    /**
     * Finds the exit structure pos by analyzing all exit structure markers
     * @return front right corner of exit structure
     */
     private static final List<Vec3i> OFFSETS = List.of(
        new Vec3i(5, 0, 5), // front right
        new Vec3i(5, 0, 0), // front left
        new Vec3i(0, 0, 5)  // back right
    );
    public BlockPos getExitStructurePos()
    {
        Block marker = ChallengeRoom.EXIT_PORTAL_BLOCK;
        List<BlockPos> markers = getPositions(marker);
        if (markers.isEmpty() || markers.size() < 4) {
            LoggerProject.logError("027001", "Exit structure markers not found in challenge area");
            return null; // No exit structure markers found
        }

        //For each block, attempt to found block in pos + (5,0,0), (0,0,5), (5,0,5)
        for (BlockPos pos : markers)
        {
            boolean allMarkersFound = true;
            for (Vec3i offset : OFFSETS) {
                BlockPos exitPos = pos.offset(offset);
                if (!markers.contains(exitPos)) {
                    allMarkersFound = false;
                }
            }
            if (allMarkersFound) return pos.below();
        }

        return null;
    }

    /**
     * Returns all portals generated programmatically internally within the challenge
     * @return
     */
    public List<Entity> getPortals() {
        return portals.stream().toList();
    }


    //** STATICS

    public static void init(EventRegistrar reg) {
        //reg.registerOnBeforeServerStarted(ChallengeKeyBlockManager::load);
    }

    static void load() {
        LoggerProject.logInit(CLASS_ID, "027000", "ChallengeKeyBlockManager");
        loadKeyBlocks();
        loadReplacementMap();
    }

    static final HashSet<Block> KEY_BLOCKS = new HashSet<>();
    static final Set<Block> WOOL_BLOCKS = new HashSet<>();
    static final Set<Block> REDSTONE_BLOCKS = new HashSet<>();
    static void loadKeyBlocks()
    {
        //All wool blocks
        WOOL_BLOCKS.addAll( List.of(
            Blocks.WHITE_WOOL,
            Blocks.ORANGE_WOOL,
            Blocks.MAGENTA_WOOL,
            Blocks.LIGHT_BLUE_WOOL,
            Blocks.YELLOW_WOOL,
            Blocks.LIME_WOOL,
            Blocks.PINK_WOOL,
            Blocks.GRAY_WOOL,
            Blocks.LIGHT_GRAY_WOOL,
            Blocks.CYAN_WOOL,
            Blocks.PURPLE_WOOL,
            Blocks.BLUE_WOOL,
            Blocks.BROWN_WOOL,
            Blocks.GREEN_WOOL,
            Blocks.RED_WOOL,
            Blocks.BLACK_WOOL
        ));

        KEY_BLOCKS.addAll(WOOL_BLOCKS);

        //Redstone blocks
        REDSTONE_BLOCKS.addAll( List.of(
            Blocks.REDSTONE_BLOCK,
            Blocks.REDSTONE_TORCH,
            Blocks.REDSTONE_WIRE,
            Blocks.REDSTONE_WALL_TORCH,
            Blocks.REPEATER,
            Blocks.COMPARATOR,
            Blocks.DETECTOR_RAIL
        ));

        KEY_BLOCKS.addAll(REDSTONE_BLOCKS);

        //Pistons
        KEY_BLOCKS.add(Blocks.PISTON);
        KEY_BLOCKS.add(Blocks.STICKY_PISTON);

        //ModBlocks - all chests
        KEY_BLOCKS.add(ModBlocks.challengeBed);
        KEY_BLOCKS.add(ModBlocks.challengeChest);
        KEY_BLOCKS.add(ModBlocks.challengeCountingChest);
        KEY_BLOCKS.add(ModBlocks.challengeSingleUseChest);

        //SpawnBlocks
        KEY_BLOCKS.add(ModBlocks.skeletonBrick);
        KEY_BLOCKS.add(ModBlocks.zombieBrick);

        //Misc
        KEY_BLOCKS.add(ChallengeRoom.EXIT_PORTAL_BLOCK);
        KEY_BLOCKS.add(Blocks.SPAWNER);
        KEY_BLOCKS.add(ModBlocks.randomBrick);

    }

    private static BlockState AIR;
    private static BlockState BRICK;
    private static final Map<Block, BlockState> R = new HashMap<>();
    private static void loadReplacementMap() {
        AIR = Blocks.AIR.defaultBlockState();
        BRICK = ModBlocks.challengeBrick.defaultBlockState();
        R.put(Blocks.PISTON, AIR);
        R.put(Blocks.STICKY_PISTON, AIR);

        //Spawner Blocks - default replaced with challenge_brick
            //ghst spawner replaced with air
            // drowned spawner replaced with water
        {
            R.put(ModBlocks.skeletonBrick, BRICK);
            R.put(ModBlocks.zombieBrick, BRICK);
        }

        //Chests
        {

        }
    }


}
