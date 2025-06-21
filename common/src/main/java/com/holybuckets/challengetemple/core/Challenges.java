package com.holybuckets.challengetemple.core;

import java.util.List;

/**
 * Loads ChallengeData from the config file and uses it to filter, generate, and return challengeIds
 */
public class Challenges {
    List<String> challengeIds;

    static String chooseChallengeId(ChallengeFilter filter) {
        return "template4x4";
    }


    class ChallengeFilter {
        // Filters for challenges based on chunkId, player data, etc.
    }

}
