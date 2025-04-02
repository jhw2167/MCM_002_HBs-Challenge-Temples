package com.holybuckets.challengetemple.core;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.model.ManagedChunk;
import com.holybuckets.foundation.model.ManagedChunkUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ManagedTemple {
    private final BlockPos position;
    private final Level level;
    private final String templeId;
    public Entity portal;
    private boolean isActive;

    public ManagedTemple(Level level, BlockPos pos) {
        this.level = level;
        this.position = pos;
        this.templeId = HBUtil.ChunkUtil.getId(pos);
        this.isActive = false;
    }

    public BlockPos getPosition() {
        return position;
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
        ManagedChunk c = ManagedChunkUtility.getManagedChunk(this.level, this.templeId);
        return c.util.isChunkFullyLoaded(c.getId());
    }

    public boolean hasPortal() {
        return portal != null;
    }
}
