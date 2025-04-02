package com.holybuckets.challengetemple.core;

import com.holybuckets.foundation.HBUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class TempleManager {
    private static TempleManager INSTANCE;
    private final Map<String, ManagedTemple> temples;

    public TempleManager() {
        this.temples = new HashMap<>();
    }

    private String generateTempleId(BlockPos blockPos) {
        return HBUtil.ChunkUtil.getId(blockPos);
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
