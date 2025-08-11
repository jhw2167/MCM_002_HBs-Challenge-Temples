package com.holybuckets.challengetemple.core;

public class ChallengeException {

    //import static com.holybuckets.challengetemple.core.ChallengeException.NotActiveChallengerException;
    public static class NotActiveChallengerException extends Exception {
        public NotActiveChallengerException(String message) {
            super(message);
        }
    }

    //import static com.holybuckets.challengetemple.core.ChallengeException.ChallengeLoadException;
    public static class ChallengeLoadException extends Exception {
        public ChallengeLoadException(String message) {
            super(message);
        }
    }

    //import static com.holybuckets.challengetemple.core.ChallengeException.ChallengeNotFoundException;
    public static class ChallengeNotFoundException extends Exception {
        public ChallengeNotFoundException(String message) {
            super(message);
        }
    }

}
