package com.holybuckets.challengetemple.core;

import com.holybuckets.foundation.HBUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class ChallengeRoom {

    private final String challengeId;
    private final BlockEntity structureBlock;
    private final Level level;

    //Statics
    private static final int CHALLENGE_DIM_HEIGHT = 66;

    //Offset for structureBlock that constructs the start challenge_room
    private static Vec3i STRUCTURE_BLOCK_OFFSET = new Vec3i(0, 0, 0);
    private static ArrayList<Vec3i> STRUCTURE_BLOCK_PIECE_OFFSETS = new ArrayList<>();
    static {
        ArrayList<Vec3i> arr = STRUCTURE_BLOCK_PIECE_OFFSETS;
        Vec3i start = STRUCTURE_BLOCK_OFFSET;
        arr.add(start.offset(0, 0, 0)); // 0
    }


    ChallengeRoom(Level level, String challengeId)
    {
        this.level = level;
        this.challengeId = challengeId;
        BlockPos worldPos = HBUtil.ChunkUtil.getWorldPos(challengeId);
        this.structureBlock = level.getBlockEntity( worldPos.offset(STRUCTURE_BLOCK_OFFSET));
    }

    /**
     * Loads the physical structure in challenge_dimension by trigering all structure blocks
     * to generate.
     * @return true if strcuture was loaded successfully, false if any issues where encountered
     */
    public boolean loadStructure() {

        List<BlockEntity> structureBlocks = new ArrayList<>();
        //load the arrayList as offsets from the original structureBlock position, use streams, no null check
        STRUCTURE_BLOCK_PIECE_OFFSETS.stream()
            .map(offset -> structureBlock.getBlockPos().offset(offset))
            .forEach(pos -> structureBlocks.add(level.getBlockEntity(pos)));


        return true;
    }

    public BlockPos getWorldPos() {
        BlockPos pos = HBUtil.ChunkUtil.getWorldPos(challengeId);
        return new BlockPos(pos.getX(), CHALLENGE_DIM_HEIGHT, pos.getZ());
    }
}
