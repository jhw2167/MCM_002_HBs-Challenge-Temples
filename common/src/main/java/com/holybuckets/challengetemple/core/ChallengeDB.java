package com.holybuckets.challengetemple.core;

import com.google.gson.JsonObject;
import com.holybuckets.challengetemple.ChallengeTempleMain;
import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.exception.NoDefaultConfig;
import net.blay09.mods.balm.api.event.server.ServerStartedEvent;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.blay09.mods.balm.api.event.server.ServerStoppedEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Loads ChallengeData from the config file and uses it to filter, generate, and return challengeIds
 */
public class ChallengeDB {

    private static final String CLASS_ID = "021";

    static List<Cursor> CHALLENGE_IDS;

    static List<Challenge> CHALLENGES;

    //Initialize the classes static events
    public static void init(EventRegistrar reg) {
        //register onServerStart, onServerStop
        reg.registerOnBeforeServerStarted(ChallengeDB::onServerStarting);
        reg.registerOnServerStopped(ChallengeDB::onServerStop);
    }

    final static String CHALLENGE_DB_PATH = "assets/hb_challenge_temple/challenges/";
    static void onServerStarting(ServerStartingEvent event)
    {
        //Load CHALLENGES from resources/assets/hb_challenge_temple/challenges/..
            //challenges.csv holds all challenge ids
            // /json holds all challenge json data, each file is a file named after challenge id
        CHALLENGE_IDS = new LinkedList<>();
        CHALLENGES = new LinkedList<>();

        //Open our challengeDb file
        Path challengesCSV = Paths.get(CHALLENGE_DB_PATH + "challenges.csv");
        String challengeDB = null;
        try {
            challengeDB = Files.readString(challengesCSV);
        } catch (Exception e) {
            if(ChallengeTempleMain.DEBUG)
                throw new RuntimeException("Failed to load challenge database from " + CHALLENGE_DB_PATH, e);
            else
                LoggerProject.logError("021000", "Failed to load challenge database from "
                 + CHALLENGE_DB_PATH + " no challenges will be loaded" );
        }

        if(challengeDB == null || challengeDB.isEmpty()) return;

        for (String line : challengeDB.split("\n"))
        {
            try {
                Cursor challengeLine = new Cursor(line);
                if(!challengeLine.doUse) continue;

                Challenge c = loadChallenge(challengeLine);
                CHALLENGES.add(c);
            } catch (NoDefaultConfig  ndc) {
                LoggerProject.logError("021001", "No default challenge json reference in: "
                + CHALLENGE_DB_PATH );
                break;
            } catch (IOException e) {
                LoggerProject.logError("021002", "Failed to load json challenge with ID: "
                + line + " from " + CHALLENGE_DB_PATH);
            }

        }


    }
        static final String DEFAULT_CHALLENGE_JSON = "CHALLENGE_DB_PATH.json";
        static Challenge loadChallenge(Cursor c) throws NoDefaultConfig, IOException
        {
            Path jsonPath = Paths.get(CHALLENGE_DB_PATH);
            final File challengeFile = jsonPath.resolve("json/" + c.challengeId + ".json").toFile();
            final File challengeDefault = jsonPath.resolve(DEFAULT_CHALLENGE_JSON).toFile();

            JsonObject json = HBUtil.FileIO.loadJsonOrDefault(challengeFile, challengeDefault);
            return Challenge.read(json);
        }

    // Remember to save complete challenges before server stop
    static void onServerStop(ServerStoppedEvent event)
    {
        // Save any necessary data or cleanup
        CHALLENGE_IDS.clear();
        CHALLENGES.clear();
    }

    /**
     * Returns a challenge abiding
     * @param filter filters acceptable list of challenges, may be null
     * @return
     */
    static Challenge chooseChallenge(@Nullable ChallengeFilter filter) {
        int rand = (int) (Math.random() * CHALLENGES.size());
        return CHALLENGES.get(rand);
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

            this.challengeId = parts[0].trim();
            this.author = parts[1].trim();
            this.challengeName = parts[2].trim();
            this.doUse = Boolean.parseBoolean(parts[3].trim());
        }
    }

}
