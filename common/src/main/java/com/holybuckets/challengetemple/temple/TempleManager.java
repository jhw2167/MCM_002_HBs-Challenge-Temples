package com.holybuckets.challengetemple.temple;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class TempleManager {
    private static TempleManager INSTANCE;
    private final Map<Integer, ManagedTemple> temples;
    private int nextTempleId;

    private TempleManager() {
        this.temples = new HashMap<>();
        this.nextTempleId = 0;
    }

    public static TempleManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TempleManager();
        }
        return INSTANCE;
    }

    public ManagedTemple registerTemple(Level level, BlockPos pos) {
        int id = nextTempleId++;
        ManagedTemple temple = new ManagedTemple(level, pos, id);
        temples.put(id, temple);
        return temple;
    }

    public void removeTemple(int id) {
        temples.remove(id);
    }

    public ManagedTemple getTemple(int id) {
        return temples.get(id);
    }

    public void clear() {
        temples.clear();
        nextTempleId = 0;
    }
}
