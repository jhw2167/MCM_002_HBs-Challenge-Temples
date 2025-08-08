package com.holybuckets.challengetemple.core;

import com.holybuckets.foundation.HBUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;


import static com.holybuckets.foundation.HBUtil.ChunkUtil;
import  static com.holybuckets.challengetemple.core.ChallengeException.NotActiveChallengerException;
import static com.holybuckets.challengetemple.core.ChallengeException.ChallengeLoadException;

/**
 * Utility methods for inerteracting with mechanics of the mod such as:
 *  - spawning specific challenges
 *  - listing available challenges
 *  - showing player completed stats
 *  - force exiting challenges
 */
public class ChallengeTempleApi {


    /**
     * Force removes player from in challenge logic
     * @param p
     * @return String error or success message to be returned to the caller
     */
     @Nullable
    public static String forceExitChallenge(Player p)
    {
        try {
            var challenger = getManagedChallenger(p);
            challenger.activeTemple.playerEndChallenge(challenger);
        } catch (NotActiveChallengerException e) {
            return e.getMessage();
        }

        return null;
    }

    public static String loadChallenge(Player p, String challengeId) {
        BlockPos pos = p.blockPosition();
        String chunkId = ChunkUtil.getId(pos);
        ChunkPos chunkPos = ChunkUtil.getChunkPos(pos);
        List<String> localChunks = ChunkUtil.getLocalChunkIds(chunkPos, 1);

        TempleManager manager = TempleManager.get(p.level());
        if( manager == null) return "No temples found in this dimension, please find a chunk with a naturally spawning temple";

        ManagedTemple nearTemple = manager.getTemple(chunkId);
        for( String id : localChunks) {
            if( nearTemple != null) break;
            nearTemple = manager.getTemple(id);
        }
        if( nearTemple == null)
            return "No temple found in nearby chunks, please be within 1 chunk of a naturally spawned temple";

        try {
            nearTemple.setChallenge(challengeId);
        } catch (ChallengeLoadException e) {
            return e.getMessage();
        }

        return "Challenge loaded successfully";
    }

    public static ManagedChallenger getManagedChallenger(Player p) throws NotActiveChallengerException
    {
        Level level = p.level();
        if (level.isClientSide())
            throw new NotActiveChallengerException("Server only request");

        ManagedChallenger mp = ManagedChallenger.getManagedChallenger(p);
        if( mp == null)
            throw new NotActiveChallengerException("No managed Challenger found for player " + p.getName().getString());

        if (mp.activeTemple == null)
            throw new NotActiveChallengerException("Player " + p.getName().getString() + " is not in an active challenge");

        return mp;
    }


}
