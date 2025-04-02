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
    public Entity portal;
    private boolean isActive;

    private static Vec3i STRUCTURE_OFFSET = new Vec3i(-3, 2, -4);

    public ManagedTemple(Level level, BlockPos pos) {
        this.level = level;
        this.portalSourcePos = pos;
        this.structurePos = pos.offset(STRUCTURE_OFFSET);
        this.templeId = HBUtil.ChunkUtil.getId(pos);
        this.isActive = false;
    }

    public BlockPos getPortalSourcePos() {
        return portalSourcePos;
    }

    public Level getLevel() {
        return level;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public String getTempleId() {
        return templeId;
    }


    //** UTILITY
    public boolean isFullyLoaded() {
        return ManagedChunkUtility.isChunkFullyLoaded(level, this.templeId);
    }

    public boolean hasPortal() {
        return portal != null;
    }
}
