package com.holybuckets.challengetemple.core;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import net.blay09.mods.balm.api.event.ChunkLoadingEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.HashMap;
import java.util.Map;

public class TempleManager {
    private static Map<LevelAccessor, TempleManager> MANAGERS;
    private final Map<String, ManagedTemple> temples;
    private final LevelAccessor level;

    public TempleManager(LevelAccessor level) {
        this.level = level;
        this.temples = new HashMap<>();

    }

    public static void init(EventRegistrar reg) {
        reg.registerOnChunkLoad(TempleManager::onChunkLoad);
        reg.registerOnChunkUnload(TempleManager::onChunkUnload);
    }

    public ManagedTemple registerTemple(Level level, BlockPos pos) {
        ManagedTemple temple = new ManagedTemple(level, pos);
        temples.put(temple.getTempleId(), temple);
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

    public static TempleManager getInstance(LevelAccessor level) {
        return MANAGERS.get(level);
    }

    public static void onChunkLoad(ChunkLoadingEvent.Load event) {
        TempleManager m = MANAGERS.get(event.getLevel());
        if (m != null) {
            m.handleChunkLoaded(event.getChunk());
        }
    }

    public static void onChunkUnload(ChunkLoadingEvent.Unload event) {
        TempleManager m = MANAGERS.get(event.getLevel());
        if (m != null) {
            m.handleChunkUnloaded(event.getChunk());
        }
    }

    private void handleChunkLoaded(ChunkAccess c) {

    }

    private void handleChunkUnloaded(ChunkAccess c) {

    }
}
