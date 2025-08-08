package com.holybuckets.challengetemple.core;

public class ChallengeException {

    public static class NotActiveChallengerException extends Exception {
        public NotActiveChallengerException(String message) {
            super(message);
        }
    }

    public static class ChallengeLoadException extends Exception {
        public ChallengeLoadException(String message) {
            super(message);
        }
    }

}
