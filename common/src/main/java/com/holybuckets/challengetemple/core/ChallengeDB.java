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


    static class ChallengeFilter {
        // Filters for challenges based on chunkId, player data, etc.
    }

    //Each line of the challenge db csv contains
    // - challengeId, author, challengeName, doUse
    static class Cursor {
        String challengeId;   
        String author;        
        String challengeName; 
        boolean doUse;        

        Cursor(String line) {
            String[] parts = line.split(",");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Invalid CSV line for Cursor: " + line);
            }
            this.challengeId = parts[0].trim();
            this.author = parts[1].trim();
            this.challengeName = parts[2].trim();
            this.doUse = Boolean.parseBoolean(parts[3].trim());
        }
    }

}
