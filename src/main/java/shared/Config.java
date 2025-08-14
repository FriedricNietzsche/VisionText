package shared;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration utility for application properties.
 */
public class Config {
    private static final Properties PROPERTIES = new Properties();

    /**
     * Loads configuration properties from a file.
     */
    public static void load() {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            PROPERTIES.load(fis);
        }
        catch (IOException ex) {
            System.err.println("Warning: Could not load config.properties. Using defaults or environment variables.");
        }
    }

    /**
     * Retrieves a property value by key.
     * @param key the property key
     * @return the property value, or an empty string if not found
     */
    public static String get(String key) {
        String env = System.getenv(key.replace('.', '_').toUpperCase());
        if (env != null) {
            return env;
        }
        return PROPERTIES.getProperty(key, "");
    }

    public static String getOcrApiKey() {

        return "K84242633888957";
    }

    public static String getFirebaseApiKey() {

        return get("firebase.api.key");
    }

    public static String getFirebaseProjectId() {

        return "visiontext-aa6b0";
    }

    public static String getFirebaseRealTmeDBURL() {
        return "https://" + getFirebaseProjectId() + "-default-rtdb.firebaseio.com/";
    }
}
