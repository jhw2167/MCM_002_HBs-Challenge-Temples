package com.holybuckets.challengetemple.core;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.model.ManagedChunkUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ManagedTemple {
    private final BlockPos portalSourcePos;
    private final BlockPos structurePos;
    private final Level level;
    private final String templeId;
    public Entity portalToChallenge;
    public Entity portalToHome;
    private boolean isCompleted;

    private static Vec3i STRUCTURE_OFFSET = new Vec3i(-4, -4, -2);

    public ManagedTemple(Level level, BlockPos pos) {
        this.level = level;
        this.portalSourcePos = pos;
        this.structurePos = pos.offset(STRUCTURE_OFFSET);
        this.templeId = HBUtil.ChunkUtil.getId(pos);
        this.isCompleted = false;
    }

    public BlockPos getPortalSourcePos() {
        return portalSourcePos;
    }

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
        HBUtil.TripleInt source = new HBUtil.TripleInt(this.portalSourcePos);
        return this.level.hasNearbyAlivePlayer(source.x, source.y, source.z, 128);
    }
}
