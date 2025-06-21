package com.holybuckets.challengetemple.core;

import java.util.List;

/**
 * Loads ChallengeData from the config file and uses it to filter, generate, and return challengeIds
 */
public class Challenges {
    static List<String> challengeIds = List.of(
        "template2x2",
        "template4x4"
    );

    static String chooseChallengeId(ChallengeFilter filter) {
        int rand = (int) (Math.random() * challengeIds.size());
        return challengeIds.get(rand);
    }


    class ChallengeFilter {
        // Filters for challenges based on chunkId, player data, etc.
    }

}
