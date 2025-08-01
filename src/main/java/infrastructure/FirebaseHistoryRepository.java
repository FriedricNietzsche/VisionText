package infrastructure;

import com.google.gson.*;
import domain.port.HistoryRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static shared.Config.getFirebaseRealTmeDBURL;
import static shared.FirebaseUtil.post;
import static shared.FirebaseUtil.get;

public class FirebaseHistoryRepository implements HistoryRepository {
    private final Gson gson;
    String FIREBASE_URL = getFirebaseRealTmeDBURL();

    public FirebaseHistoryRepository() {
        this.gson = new Gson();
    }

    private String encodeUsername(String username) {
        // Handle null username
        if (username == null) {
            return null;
        }
        // Replace @ and . with safe characters for Firebase keys
        return username.replace("@", "_AT_").replace(".", "_DOT_");
    }

    @Override
    public void saveHistory(String username, String imageFilename, String textContent, long timestamp) throws IOException {
        // Validate input parameters
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (imageFilename == null || imageFilename.isEmpty()) {
            throw new IllegalArgumentException("Image filename cannot be null or empty");
        }
        if (textContent == null) {
            throw new IllegalArgumentException("Text content cannot be null");
        }

        // Encode username to handle special characters and fix URL construction
        String encodedUsername = encodeUsername(username);
        String url = FIREBASE_URL + "users/" + encodedUsername + "/history.json";

        try {
            // Create the data structure for Realtime DB (plain JSON, not Firestore format)
            Map<String, Object> historyItem = new LinkedHashMap<>();
            historyItem.put("filename", imageFilename);
            historyItem.put("content", textContent);
            historyItem.put("timestamp", timestamp);

            String jsonPayload = gson.toJson(historyItem);

            // POST to the .json endpoint to auto-generate a key
            String response = post(url, jsonPayload);

            if (response == null || response.isEmpty()) {
                throw new IOException("Empty response from Firebase");
            }
            // Optionally, parse response to check for name (the new key)
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            if (!jsonResponse.has("name")) {
                throw new IOException("Failed to save history: " + response);
            }
        } catch (JsonSyntaxException e) {
            throw new IOException("Failed to parse Firebase response", e);
        } catch (Exception e) {
            throw new IOException("Failed to save history", e);
        }
    }

    @Override
    public List<String> getHistoryList(String username) {
        // Handle null username early
        if (username == null || username.isEmpty()) {
            System.err.println("Username is null or empty, returning empty history list");
            return new ArrayList<>();
        }

        String encodedUsername = encodeUsername(username);
        String url = FIREBASE_URL + "users/" + encodedUsername + "/history.json";
        List<String> historyList = new ArrayList<>();

        try {
            String jsonResponse = get(url);

            if (jsonResponse == null || jsonResponse.isEmpty() || jsonResponse.equals("null")) {
                throw new RuntimeException("Empty response from Firebase");
            }

            JsonObject response = gson.fromJson(jsonResponse, JsonObject.class);
            if (response == null) {
                throw new RuntimeException("Failed to parse Firebase response");
            }

            for (Map.Entry<String, JsonElement> entry : response.entrySet()) {
                JsonObject item = entry.getValue().getAsJsonObject();
                String filename = item.has("filename") ? item.get("filename").getAsString() : "unknown";
                String timestamp = item.has("timestamp") ? item.get("timestamp").getAsString() : "unknown";
                historyList.add(filename + " (" + timestamp + ")");
            }
        } catch (Exception e) {
            System.err.println("Error fetching history: " + e.getMessage());
            e.printStackTrace();
        }
        return historyList;
    }

    @Override
    public String getHistoryItem(String username, String historyId) throws IOException {
        // Handle null username
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (historyId == null || historyId.isEmpty()) {
            throw new IllegalArgumentException("History ID cannot be null or empty");
        }

        String encodedUsername = encodeUsername(username);
        String url = FIREBASE_URL + "users/" + encodedUsername + "/history/" + historyId + ".json";
        try {
            String jsonResponse = get(url);
            if (jsonResponse == null || jsonResponse.isEmpty() || jsonResponse.equals("null")) {
                return null;
            }
            JsonObject item = gson.fromJson(jsonResponse, JsonObject.class);
            if (item == null || !item.has("content")) {
                return null;
            }
            return item.get("content").getAsString();
        } catch (JsonSyntaxException e) {
            throw new IOException("Failed to parse history item JSON", e);
        } catch (IllegalStateException e) {
            throw new IOException("Invalid history item data structure", e);
        }
    }
}
