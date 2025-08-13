package shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void loadDoesNotThrowWhenMissingFile() {
        assertDoesNotThrow(Config::load);
    }

    @Test
    void getFallsBackToEnvOrEmpty() {
        // This key likely doesn't exist in env
        String v = Config.get("non.existent.key");
        assertNotNull(v);
    }

    @Test
    void firebaseHelpersReturnStrings() {
        assertNotNull(Config.getOcrApiKey());
        assertNotNull(Config.getFirebaseApiKey());
        assertTrue(Config.getFirebaseRealTmeDBURL().contains("firebaseio.com"));
    }
}
