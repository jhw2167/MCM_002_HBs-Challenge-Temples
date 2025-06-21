package com.holybuckets.challengetemple.core;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.model.ManagedChunkUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ManagedTemple {

    private static final String CLASS_ID = "006"; // Class ID for logging purposes

    private final BlockPos entityPos;
    private final BlockPos structurePos;
    private final BlockPos portalSourcePos;

    private final Level level;
    private final String templeId;
    private ChallengeRoom challengeRoom;
    public Entity portalToChallenge;
    public Entity portalToHome;
    private boolean isCompleted;

    private static Vec3i STRUCTURE_OFFSET = new Vec3i(-4, -4, -2);

    private static final Vec3i SOURCE_OFFSET = new Vec3i(0, -1, 1);
    private static final Vec3i DEST_OFFSET = new Vec3i(2, 2, 1);
    private static final int CHALLENGE_DIM_HEIGHT = 64;


    public ManagedTemple(Level level, BlockPos pos) {
        this.level = level;
        this.entityPos = pos;
        this.portalSourcePos = pos.offset(SOURCE_OFFSET);
        this.structurePos = pos.offset(STRUCTURE_OFFSET);

        this.templeId = HBUtil.ChunkUtil.getId(pos);
        this.challengeRoom = new ChallengeRoom(this.templeId);
        this.isCompleted = false;
    }

    public BlockPos getPortalSourcePos() {
        return this.portalSourcePos;
    }

    public BlockPos getStructurePos() {
        return structurePos;
    }

    public BlockPos getPortalDest() {
        return challengeRoom.getWorldPos().offset(DEST_OFFSET);
    }

    public BlockPos getEntityPos() { return entityPos; }

    public Level getLevel() {
        return level;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted() {
        this.isCompleted = true;
        this.portalToChallenge = null;
        this.portalToHome = null;
    }

    public String getTempleId() {
        return templeId;
    }

    //** UTILITY
    public boolean isFullyLoaded() {
        return ManagedChunkUtility.isChunkFullyLoaded(level, this.templeId);
    }

    public boolean hasPortal() {
        return portalToChallenge != null;
    }

    public boolean hasNearPlayer() {
        HBUtil.TripleInt source = new HBUtil.TripleInt(this.entityPos);
        return this.level.hasNearbyAlivePlayer(source.x, source.y, source.z, 128);
    }

    public void buildChallenge() {
        if (this.challengeRoom != null)
            this.challengeRoom.loadStructure();
    }
}
