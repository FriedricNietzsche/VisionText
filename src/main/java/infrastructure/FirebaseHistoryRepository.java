

package infrastructure;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.port.HistoryRepository;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static shared.Config.getFirebaseProjectId;
import static shared.FirebaseUtil.get;
import static shared.FirebaseUtil.post;


public class FirebaseHistoryRepository implements HistoryRepository {
    private static final String FIREBASE_PROJECT_ID = getFirebaseProjectId();
    private static final String FIREBASE_URL = "https://firebase.google.com/docs/web/setup#available-libraries" + FIREBASE_PROJECT_ID;
    private final Gson gson;


    public FirebaseHistoryRepository() {
        this.gson = new Gson();
    }

    @Override
    public void saveHistory(String username, String imageFilename, String textContent, long timestamp) throws IOException {
        // TODO: Implement Firestore save via REST or Admin SDK
        // construct url
        String url = FIREBASE_URL + "/users/" + username + "/history";

        // documentation according to firebase REST API JSON structure
        Map<String, Object> historyDoc = Map.of("fields", Map.of("filename", Map.of("stringValue", imageFilename), "content", Map.of("stringValue", textContent), "timestamp", Map.of("integerValue", String.valueOf(timestamp))));
        // Convert map to  JSON string suitable for HTTP POST
        String json = gson.toJson(historyDoc);
        // Build post requests
        String post_request = post(url, json);
        System.out.println(post_request);
    }

    @Override
    public List<String> getHistoryList(String username) {
        // TODO: Return list of history item IDs or summaries for user

        String url = FIREBASE_URL + "/users/" + username + "/history";
        // list to hold summaries

        List<String> historyList = new ArrayList<>();

        try {
            // Fetch response
            String jsonResponse = get(url);
            // Parse JSON into Map
            Map<String, Object> historyDoc = gson.fromJson(jsonResponse, new TypeToken<Map<String, Object>>() {
            }.getType());
            // Extract document list
            List<Map<String, Object>> history = (List<Map<String, Object>>) historyDoc.get("content");
            if (history != null) {
                for (Map<String, Object> historyMap : history) {
                    String filename = (String) ((Map<String, Object>) ((Map<String, Object>) historyMap.get("fields")).get("filename")).get("stringValue");
                    String timestampStr = (String) ((Map<String, Object>) ((Map<String, Object>) historyMap.get("fields")).get("timestamp")).get("integerValue");

                    historyList.add(filename + "(" + timestampStr + ")");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return historyList;
    }

    @Override
    public String getHistoryItem(String username, String historyId) throws IOException {
        // TODO: Return text content for a specific history item
        String url = FIREBASE_URL + "/users/" + username + "/history/" + historyId;
        String jsonResponse = get(url);

        Map<String, Object> historyDoc = gson.fromJson(jsonResponse, new TypeToken<Map<String, Object>>() {
        }.getType());

        Map<String, Object> fields = (Map<String, Object>) historyDoc.get("fields");
        if (fields == null) {
            return null;
        }
        Map<String, Object> contentField = (Map<String, Object>) fields.get("content");
        if (contentField == null) {
            return null;
        }
        return (String) contentField.get("stringValue");
    }
}

