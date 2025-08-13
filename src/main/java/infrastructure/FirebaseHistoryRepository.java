package infrastructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import domain.port.HistoryRepository;
import shared.Config;
import shared.FirebaseUtil;

/**
 * Firebase history repository implementation.
 */
public class FirebaseHistoryRepository implements HistoryRepository {
    // Static constants
    private static final String USERS_PATH = "users/";
    private static final String FILENAME_KEY = "filename";
    private static final String CONTENT_KEY = "content";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String USERNAME_NULL_MSG = "Username cannot be null or empty";
    private static final String HISTORY_PATH = "/history.json";
    private static final String HISTORY_ITEM_PATH = "/history/";
    private static final String HISTORY_ITEM_SUFFIX = ".json";
    private static final String SEPARATOR = "";
    private static final String UNKNOWN_FILENAME = "unknown";
    private static final String EMPTY_RESPONSE_MSG = "Empty response from Firebase";
    private static final String FAILED_PARSE_MSG = "Failed to parse Firebase response";
    private static final String FAILED_SAVE_MSG = "Failed to save history: ";
    private static final String HISTORY_ID_NULL_MSG = "History ID cannot be null or empty";

    // Instance variables
    private final Gson gson;
    private final String firebaseUrl;

    public FirebaseHistoryRepository() {
        this.gson = new Gson();
        this.firebaseUrl = Config.getFirebaseRealTmeDBURL();
    }

    private String encodeUsername(String username) {
        if (username == null) {
            return null;
        }
        return username.replace("@", "_AT_").replace(".", "_DOT_");
    }

    private String getActualKey(String historyId) {
        if (historyId != null) {
            String[] parts = historyId.split(SEPARATOR);
            if (parts.length > 0) {
                return parts[0];
            }
        }
        return historyId;
    }

    private void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException(USERNAME_NULL_MSG);
        }
    }

    @Override
    public void saveHistory(String username, String imageFilename, String textContent, long timestamp)
            throws IOException {
        validateUsername(username);
        if (textContent == null) {
            throw new IllegalArgumentException("Text content cannot be null");
        }
        String encodedUsername = encodeUsername(username);
        Map<String, Object> historyItem = new LinkedHashMap<>();
        historyItem.put(FILENAME_KEY, Objects.requireNonNullElse(imageFilename, ""));
        historyItem.put(CONTENT_KEY, textContent);
        historyItem.put(TIMESTAMP_KEY, timestamp);
        String jsonPayload = gson.toJson(historyItem);
        String response = FirebaseUtil.post(firebaseUrl + USERS_PATH + encodedUsername + HISTORY_PATH, jsonPayload);
        if (response.isEmpty()) {
            throw new IOException(EMPTY_RESPONSE_MSG);
        }
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
        if (jsonResponse == null || !jsonResponse.has("name")) {
            throw new IOException(FAILED_SAVE_MSG + response);
        }
    }

    @Override
    public List<String> getHistoryList(String username) {
        validateUsername(username);
        String encodedUsername = encodeUsername(username);
        String url = firebaseUrl + USERS_PATH + encodedUsername + HISTORY_PATH;
        List<String> historyList = new ArrayList<>();
        String jsonResponse;
        try {
            jsonResponse = FirebaseUtil.get(url);
        }
        catch (IOException ex) {
            throw new RuntimeException("Error fetching history: " + ex.getMessage(), ex);
        }
        if (jsonResponse.isEmpty() || "null".equals(jsonResponse)) {
            return historyList;
        }
        JsonObject response = gson.fromJson(jsonResponse, JsonObject.class);
        if (response == null) {
            throw new RuntimeException(FAILED_PARSE_MSG);
        }
        for (Map.Entry<String, JsonElement> entry : response.entrySet()) {
            String firebaseKey = entry.getKey();
            JsonObject item = entry.getValue().getAsJsonObject();
            String filename;
            if (item.has(FILENAME_KEY)) {
                filename = item.get(FILENAME_KEY).getAsString();
            }
            else {
                filename = UNKNOWN_FILENAME;
            }
            long timestamp = 0L;
            if (item.has(TIMESTAMP_KEY)) {
                try {
                    timestamp = item.get(TIMESTAMP_KEY).getAsLong();
                }
                catch (NumberFormatException | IllegalStateException ex) {
                    // ignore
                }
            }
            String displayString = filename + " (" + timestamp + ")";
            historyList.add(firebaseKey + SEPARATOR + timestamp + SEPARATOR + displayString);
        }
        return historyList;
    }

    @Override
    public String getHistoryItem(String username, String historyId) throws IOException {
        validateUsername(username);
        if (historyId == null || historyId.isEmpty()) {
            throw new IllegalArgumentException(HISTORY_ID_NULL_MSG);
        }
        String actualKey = getActualKey(historyId);
        String encodedUsername = encodeUsername(username);
        String url = firebaseUrl + USERS_PATH + encodedUsername + HISTORY_ITEM_PATH + actualKey + HISTORY_ITEM_SUFFIX;
        String jsonResponse = FirebaseUtil.get(url);
        if (jsonResponse.isEmpty() || "null".equals(jsonResponse)) {
            return null;
        }
        JsonObject item = gson.fromJson(jsonResponse, JsonObject.class);
        if (item == null || !item.has(CONTENT_KEY)) {
            return null;
        }
        return item.get(CONTENT_KEY).getAsString();
    }

    @Override
    public void deleteHistory(String username, String historyId) throws IOException {
        validateUsername(username);
        if (historyId == null || historyId.isEmpty()) {
            throw new IllegalArgumentException(HISTORY_ID_NULL_MSG);
        }
        String actualKey = getActualKey(historyId);
        String encodedUsername = encodeUsername(username);
        String url = firebaseUrl + USERS_PATH + encodedUsername + HISTORY_ITEM_PATH + actualKey + HISTORY_ITEM_SUFFIX;
        FirebaseUtil.delete(url);
    }
}
