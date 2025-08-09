package infrastructure;

import com.google.gson.*;
import domain.port.HistoryRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static shared.Config.getFirebaseRealTmeDBURL;
import static shared.FirebaseUtil.get;
import static shared.FirebaseUtil.post;
import static shared.FirebaseUtil.delete;

public class FirebaseHistoryRepository implements HistoryRepository {
    private final Gson gson;
    private final String FIREBASE_URL = getFirebaseRealTmeDBURL();

    public FirebaseHistoryRepository() {
        this.gson = new Gson();
    }

    private String encodeUsername(String username) {
        if (username == null) return null;
        // Firebase keys can't contain '.', '#', '$', '[', ']' — you already replace @ and .
        // Keep your convention for consistency:
        return username.replace("@", "_AT_").replace(".", "_DOT_");
    }

    @Override
    public void saveHistory(String username, String imageFilename, String textContent, long timestamp) throws IOException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        // Allow empty filename (text-only items are valid)
        if (textContent == null) {
            throw new IllegalArgumentException("Text content cannot be null");
        }

        String encodedUsername = encodeUsername(username);
        String url = FIREBASE_URL + "users/" + encodedUsername + "/history.json";

        try {
            Map<String, Object> historyItem = new LinkedHashMap<>();
            historyItem.put("filename", imageFilename == null ? "" : imageFilename);
            historyItem.put("content", textContent);
            historyItem.put("timestamp", timestamp);

            String jsonPayload = gson.toJson(historyItem);
            String response = post(url, jsonPayload);

            if (response == null || response.isEmpty()) {
                throw new IOException("Empty response from Firebase");
            }
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            if (jsonResponse == null || !jsonResponse.has("name")) {
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
                // no history yet
                return historyList;
            }

            JsonObject response = gson.fromJson(jsonResponse, JsonObject.class);
            if (response == null) {
                throw new RuntimeException("Failed to parse Firebase response");
            }

            for (Map.Entry<String, JsonElement> entry : response.entrySet()) {
                String firebaseKey = entry.getKey();
                JsonObject item = entry.getValue().getAsJsonObject();

                String filename = item.has("filename") ? item.get("filename").getAsString() : "unknown";
                long timestamp = 0L;
                if (item.has("timestamp")) {
                    try { timestamp = item.get("timestamp").getAsLong(); } catch (Exception ignored) {}
                }

                // What the user sees in the list
                String displayString = filename + " (" + timestamp + ")";

                // Return with timestamp in the middle for date-sorting:
                // format: key|||epochMillis|||display
                historyList.add(firebaseKey + "|||" + timestamp + "|||" + displayString);
            }
        } catch (Exception e) {
            System.err.println("Error fetching history: " + e.getMessage());
            e.printStackTrace();
        }
        return historyList;
    }

    @Override
    public String getHistoryItem(String username, String historyId) throws IOException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (historyId == null || historyId.isEmpty()) {
            throw new IllegalArgumentException("History ID cannot be null or empty");
        }

        // Extract the actual Firebase key (historyId can be "key|||display" or "key|||epoch|||display")
        String actualKey = historyId;
        if (historyId.contains("|||")) {
            actualKey = historyId.split("\\|\\|\\|")[0];
        }

        String encodedUsername = encodeUsername(username);
        String url = FIREBASE_URL + "users/" + encodedUsername + "/history/" + actualKey + ".json";
        try {
            String jsonResponse = get(url);
            if (jsonResponse == null || jsonResponse.isEmpty() || "null".equals(jsonResponse)) {
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

    @Override
    public void deleteHistory(String username, String historyId) throws IOException {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (historyId == null || historyId.isEmpty()) {
            throw new IllegalArgumentException("History ID cannot be null or empty");
        }

        // Extract firebase key if a composite id was passed
        String actualKey = historyId;
        if (historyId.contains("|||")) {
            actualKey = historyId.split("\\|\\|\\|")[0];
        }

        String encodedUsername = encodeUsername(username);
        String url = FIREBASE_URL + "users/" + encodedUsername + "/history/" + actualKey + ".json";

        try {
            // Firebase Realtime DB: DELETE the node
            delete(url);
        } catch (Exception e) {
            throw new IOException("Failed to delete history item", e);
        }
    }
}
