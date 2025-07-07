package com.holybuckets.challengetemple.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.holybuckets.challengetemple.ChallengeTempleMain;
import com.holybuckets.challengetemple.Constants;
import com.holybuckets.challengetemple.LoggerProject;
import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.exception.NoDefaultConfig;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.server.ServerStartedEvent;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.blay09.mods.balm.api.event.server.ServerStoppedEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
        //reg.registerOnBeforeServerStarted(ChallengeDB::onServerStarting);
        reg.registerOnServerStarted(ChallengeDB::onServerStarted, EventPriority.High);
        reg.registerOnServerStopped(ChallengeDB::onServerStop);
    }

    //final static String CHALLENGE_DB_PATH = "assets/hb_challenge_temple/challenges/";
    final static ResourceLocation CHALLENGE_DB_PATH = new ResourceLocation(Constants.MOD_ID, "challenges");
    final static String CHALLENGES_CSV = "challenges/challenges.csv";
    final static String CHALLENGES_JSON = "challenges/json";
    final static String CHALLENGES_DEFAULT_JSON = "challenges/json/challenge_template.json";

    //static void onServerStarting(ServerStartingEvent event)
    static void onServerStarted(ServerStartedEvent event)
    {
        //Load CHALLENGES from resources/assets/hb_challenge_temple/challenges/..
            //challenges.csv holds all challenge ids
            // /json holds all challenge json data, each file is a file named after challenge id
        CHALLENGE_IDS = new LinkedList<>();
        CHALLENGES = new LinkedList<>();

        ResourceManager manager = GeneralConfig.getInstance()
            .getServer().getResourceManager();


        //Open our challengeDb file
        Resource challengesCSV = manager.listResources(CHALLENGES_CSV, p -> true)
        .get( new ResourceLocation(CHALLENGE_DB_PATH.getNamespace(), CHALLENGES_CSV) );
        String challengeDB = null;

        try(InputStream is = challengesCSV.open()) {
            challengeDB = new String(is.readAllBytes());
        } catch (Exception e) {
            if(ChallengeTempleMain.DEBUG)
                throw new RuntimeException("Failed to load challenge database from " + CHALLENGE_DB_PATH, e);
            else
                LoggerProject.logError("021000", "Failed to load challenge database from "
                 + CHALLENGE_DB_PATH + " no challenges will be loaded" );
        }

        if(challengeDB == null || challengeDB.isEmpty()) return;

        Map<ResourceLocation, Resource> jsonChallenges = manager.listResources("challenges/json", p -> true);
        ResourceLocation defaultJsonLocation = new ResourceLocation(CHALLENGE_DB_PATH.getNamespace(),
            CHALLENGES_DEFAULT_JSON);
        String challengeDefaultJson = null;
        try(InputStream is = jsonChallenges.get(defaultJsonLocation).open()) {
            challengeDefaultJson = new String(is.readAllBytes());
        } catch (IOException e) {
            LoggerProject.logError("021003", "Failed to load default challenge json" + CHALLENGES_DEFAULT_JSON );
            return;
        }
        String lineDelim = "\n";
        if( challengeDB.contains("\r\n") )
            lineDelim = "\r\n"; // Windows line endings
        String[] dbLines = challengeDB.split(lineDelim);
        for (int i = 0; i<dbLines.length; i++)
        {
            if(i==0) continue; // Skip header line
            String line = dbLines[i].trim();
            Cursor c = new Cursor(line);
            CHALLENGE_IDS.add(c);
            if(!c.doUse) continue;

            ResourceLocation locationWithId = new ResourceLocation(
                CHALLENGE_DB_PATH.getNamespace(),
                CHALLENGES_JSON + "/" + c.challengeId + ".json");
           Resource challengeJson = jsonChallenges.get(locationWithId);
            try(InputStream is = challengeJson.open())
            {
                Challenge challenge = loadChallenge(c, new String(is.readAllBytes()), challengeDefaultJson );
                CHALLENGES.add(challenge);
            } catch (NoDefaultConfig  ndc) {
                LoggerProject.logError("021001", "No default challenge json reference in: "
                + CHALLENGE_DB_PATH );
                break;
            } catch (IOException e) {
                LoggerProject.logError("021002", "Failed to load json challenge with ID: "
                + line + " from " + CHALLENGE_DB_PATH);
            } catch (Exception e) {
                LoggerProject.logError("021004", "Failed to load challenge with ID: "
                + line + " from " + CHALLENGE_DB_PATH);
            }

        }


    }
        static final String DEFAULT_CHALLENGE_JSON = "CHALLENGE_DB_PATH.json";
        static Challenge loadChallenge(Cursor c, String json, String defaultJson) throws NoDefaultConfig, IOException
        {
            //Path jsonPath = Paths.get(CHALLENGE_DB_PATH.getPath());
            //final File challengeFile = jsonPath.resolve("json/" + c.challengeId + ".json").toFile();
            //final File challengeDefault = jsonPath.resolve(DEFAULT_CHALLENGE_JSON).toFile();
            final JsonObject challengeJson = new JsonParser().parse(json).getAsJsonObject();
            final JsonObject challengeDefJson = new JsonParser().parse(defaultJson).getAsJsonObject();

            JsonObject result = HBUtil.FileIO.loadJsonOrDefault(challengeJson, challengeDefJson);
            return Challenge.read( result );
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
        if(CHALLENGES.isEmpty()) {
            return null; // or throw an exception
        }
        int rand = (int) (Math.random() * CHALLENGES.size());
        return CHALLENGES.get(rand);
    }


    static class ChallengeFilter {
        String challengeId;

        public ChallengeFilter setChallengeId(String challengeId) {
            this.challengeId = challengeId;
            return this;
        }
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
            if(parts[3].trim().equals("1")) this.doUse = true;
        }
    }

}
