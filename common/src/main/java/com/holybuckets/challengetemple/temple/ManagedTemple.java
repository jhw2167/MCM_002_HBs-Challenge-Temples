package com.holybuckets.challengetemple.temple;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ManagedTemple {
    private final BlockPos position;
    private final Level level;
    private boolean isActive;
    private int templeId;

    public ManagedTemple(Level level, BlockPos pos, int id) {
        this.level = level;
        this.position = pos;
        this.templeId = id;
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

    public int getTempleId() {
        return templeId;
    }
}
