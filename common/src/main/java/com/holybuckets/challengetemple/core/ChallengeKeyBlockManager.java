package com.holybuckets.challengetemple.core;

import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.challengetemple.block.ModBlocks;
import com.holybuckets.foundation.event.EventRegistrar;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

import static com.holybuckets.challengetemple.core.TempleManager.CHALLENGE_LEVEL;


/**
 * Some blocks in a challenge have special properties that require
 * programatic handling, we will handle them here.
 */
public class ChallengeKeyBlockManager {

    public static final String CLASS_ID = "027";

    final Map<Block, List<BlockPos>> BLOCKS;
    final BlockPos startPos;
    final Vec3i size;
    boolean loaded;

    /**
     * Creates and load array of key blocks in the challenge area.
     * @param startPos
     * @param size
     */
    public ChallengeKeyBlockManager(BlockPos startPos, Vec3i size) {
        this.BLOCKS = new HashMap<>();
        this.startPos = startPos;
        this.size = size;
        this.loaded = false;
        loadChallengeBlocks();
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


    }

    public List<BlockPos> getPositions(Block b) {
        return BLOCKS.getOrDefault(b, Collections.emptyList()).stream().toList();
    }

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
        return null;
    }


    //** STATICS

    public static void init(EventRegistrar reg) {
        reg.registerOnBeforeServerStarted(ChallengeKeyBlockManager::loadKeyBlocks);
    }

    static final HashSet<Block> KEY_BLOCKS = new HashSet<>();
    static void loadKeyBlocks(ServerStartingEvent event) {
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


}
