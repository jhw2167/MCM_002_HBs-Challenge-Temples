package com.holybuckets.challengetemple.temple;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class TempleManager {
    private static TempleManager INSTANCE;
    private final Map<String, ManagedTemple> temples;

    private TempleManager() {
        this.temples = new HashMap<>();
    }

    public static TempleManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TempleManager();
        }
        return INSTANCE;
    }

    private String generateTempleId(BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        return String.format("%d_%d", chunkX, chunkZ);
    }

    public ManagedTemple registerTemple(Level level, BlockPos pos) {
        String id = generateTempleId(pos);
        ManagedTemple temple = new ManagedTemple(level, pos, id);
        temples.put(id, temple);
        return temple;
    }

    public void removeTemple(String id) {
        temples.remove(id);
    }

    public ManagedTemple getTemple(String id) {
        return temples.get(id);
    }

    public void clear() {
        temples.clear();
    }
}
