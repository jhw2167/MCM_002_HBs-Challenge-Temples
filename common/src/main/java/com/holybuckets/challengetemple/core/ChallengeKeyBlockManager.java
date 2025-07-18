package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.holybuckets.challengetemple.core.TempleManager.CHALLENGE_LEVEL;
import static com.holybuckets.challengetemple.core.TempleManager.PORTAL_API;
import static com.holybuckets.foundation.HBUtil.BlockUtil;


/**
 * Some blocks in a challenge have special properties that require
 * programatic handling, we will handle them here.
 */
public class ChallengeKeyBlockManager {

    public static final String CLASS_ID = "027";

    private final Map<Block, List<BlockPos>> BLOCKS;
    private final Map<BlockState, List<BlockPos>> REPLACERS;
    private final List<Entity> PORTALS;
    private final ServerLevel level;
    final BlockPos startPos;
    final Vec3i size;
    boolean loaded;

    /**
     * Creates and load array of key blocks in the challenge area.
     * @param startPos
     * @param size
     */
    public ChallengeKeyBlockManager(ServerLevel level, BlockPos startPos, Vec3i size)
    {
        this.BLOCKS = new HashMap<>();
        this.REPLACERS = new HashMap<>();
        this.PORTALS = new ArrayList<>();
        this.level = level;
        this.startPos = startPos;
        this.size = size;
        this.loaded = false;

        initReplacers();
        loadChallengeBlocks();
    }

    private void initReplacers() {
        //Initialize replacers
        REPLACERS.put(Blocks.AIR.defaultBlockState(), new LinkedList<>());
        REPLACERS.put(ModBlocks.challengeBrick.defaultBlockState(), new LinkedList<>());
    }

    private void loadChallengeBlocks()
    {
        //Load all key blocks
        for (Block block : KEY_BLOCKS) {
            BLOCKS.put(block, new LinkedList<>());
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
                        BLOCKS.get(block).add(pos);
                    }
                }
            }
        }

        generatePortals();
    }


    private void generatePortals()
    {
        List<Block> woolBlocks = new ArrayList<>();
        for (Block block : BLOCKS.keySet())
        {
            if( BLOCKS.get(block).isEmpty() ) continue;
            String name = BlockUtil.blockToString(block);
            if( name.endsWith("_wool") ) woolBlocks.add(block);
        }

        for( Block wool : woolBlocks )
        {
            List<BlockPos> pistons = BLOCKS.getOrDefault(Blocks.PISTON, Collections.emptyList());
            List<BlockPos> stickyPistons = BLOCKS.getOrDefault(Blocks.STICKY_PISTON, Collections.emptyList());
            REPLACERS.get(R.get(Blocks.PISTON)).addAll(pistons);
            REPLACERS.get(R.get(Blocks.STICKY_PISTON)).addAll(stickyPistons);

            if( pistons.isEmpty() || stickyPistons.isEmpty() ) continue;

            Map<BlockPos, Pair<Integer, Direction>> sourcePortals = new HashMap<>();
            for( BlockPos woolPos : BLOCKS.get(wool) ) {
                Pair<Integer, Direction> pistonInfo = findPistonsAboveBelow(woolPos, Blocks.STICKY_PISTON);
                if( pistonInfo == null ) continue; // no pistons above or below
                sourcePortals.put(woolPos, pistonInfo);
            }

            if( sourcePortals.isEmpty() ) continue; // no wool blocks with pistons above or below

            Map<BlockPos, Pair<Integer, Direction>> destPortals = new HashMap<>();
            for( BlockPos woolPos : BLOCKS.get(wool) ) {
                Pair<Integer, Direction> pistonInfo = findPistonsAboveBelow(woolPos, Blocks.PISTON);
                if( pistonInfo == null ) continue; // no pistons above or below
                destPortals.put(woolPos, pistonInfo);
            }

            //Determine the plane the sourcePortals are in
            int xWidth = 0, zWidth = 0;
            //find the max difference in the x dimension and the z dimension using a nested for loop
            BlockPos[] positions = sourcePortals.keySet().toArray(new BlockPos[0]);
            BlockPos topLeft = positions[0];
            for (int i = 0; i < positions.length; i++) {
                if( positions[i].getX() < topLeft.getX() || positions[i].getZ() > topLeft.getZ() )
                    topLeft = positions[i];

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
            Direction dir = null;
            if(xWidth == 0) {  //portal in z dimension
                width = zWidth;
                height = Math.abs( sourcePortals.get(positions[0]).getLeft() )-1;
                dir = sourcePortals.get(positions[0]).getRight();
            }
            else if(zWidth == 0) { //portal in x dimension
                width = xWidth;
                height = Math.abs( sourcePortals.get(positions[0]).getLeft() )-1;
                dir = sourcePortals.get(positions[0]).getRight();
            }
            else {  //portal is up or down
               height = xWidth;
               width = zWidth;
               dir = sourcePortals.get(positions[0]).getRight();
            }

            //Portal starts in top left, most negative x, most positive z, highest y

            //Create the portal entity
            Entity portals = PORTAL_API.createPortal(
                level, topLeft, width, height, dir
            );

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
        while( next.is(type) ) {
            pos = pos.offset(0, dir, 0);
            next = level.getBlockState(pos);
        }

        return Pair.of((pos.getY() - woolPos.getY()), next.getValue(BlockStateProperties.FACING) );
    }



    public List<BlockPos> getPositions(Block b) {
        return BLOCKS.getOrDefault(b, Collections.emptyList()).stream().toList();
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
    public BlockPos getExitStructurePos() {
        BlockState marker = ChallengeRoom.EXIT_PORTAL_BLOCK;
        List<BlockPos> markers = getPositions(marker.getBlock());
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
            if (allMarkersFound) return pos;
        }

        return null;
    }

    /**
     * Returns all portals generated programmatically internally within the challenge
     * @return
     */
    public List<Entity> getPortals() {
        return PORTALS.stream().toList();
    }


    //** STATICS

    public static void init(EventRegistrar reg) {
        reg.registerOnBeforeServerStarted(ChallengeKeyBlockManager::onServerStarting);
    }

    static void onServerStarting(ServerStartingEvent event) {
        LoggerProject.logInit(CLASS_ID, "027000", "ChallengeKeyBlockManager");
        loadKeyBlocks();
        loadReplacementMap();
    }

    static final HashSet<Block> KEY_BLOCKS = new HashSet<>();
    static void loadKeyBlocks()
    {
        //All wool blocks
        final var WOOL_BLOCKS = List.of(
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
        );

        KEY_BLOCKS.addAll(WOOL_BLOCKS);

        //Pistons
        KEY_BLOCKS.add(Blocks.PISTON);
        KEY_BLOCKS.add(Blocks.STICKY_PISTON);

        //ModBlocks - all chests
        KEY_BLOCKS.add(ModBlocks.challengeBed);
        KEY_BLOCKS.add(ModBlocks.challengeChest);
        KEY_BLOCKS.add(ModBlocks.challengeCountingChest);
        KEY_BLOCKS.add(ModBlocks.challengeSingleUseChest);

        //Misc
        KEY_BLOCKS.add(Blocks.SOUL_TORCH);
        KEY_BLOCKS.add(Blocks.SPAWNER);

    }

    private static final Map<Block, BlockState> R = new HashMap<>();
    private static void loadReplacementMap() {
        BlockState A = Blocks.AIR.defaultBlockState();
        BlockState B = ModBlocks.challengeBrick.defaultBlockState();
        R.put(Blocks.PISTON, A);
        R.put(Blocks.STICKY_PISTON, A);
    }



}
