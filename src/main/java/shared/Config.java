package shared;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Properties props = new Properties();


    public static void load() {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("Warning: Could not load config.properties. Using defaults or environment variables.");
        }
    }

    public static String get(String key) {
        String env = System.getenv(key.replace('.', '_').toUpperCase());
        if (env != null) return env;
        return props.getProperty(key, "");
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
        return  "https://" + getFirebaseProjectId() + "-default-rtdb.firebaseio.com/";
    }

}
