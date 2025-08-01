package infrastructure;

import com.google.gson.*;
import domain.port.HistoryRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static shared.Config.get;
import static shared.Config.getFirebaseRealTmeDBURL;
import static shared.FirebaseUtil.post;


public class FirebaseHistoryRepository implements HistoryRepository {
    private final Gson gson;
    String FIREBASE_URL = getFirebaseRealTmeDBURL();

    public FirebaseHistoryRepository() {
        this.gson = new Gson();
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

        // Construct Firestore document URL with auto-generated ID
        String url = FIREBASE_URL + "/users/" + username + "/history?documentId=auto";

        try {
            // Create the document structure according to Firestore REST API
            Map<String, Object> documentFields = new LinkedHashMap<>();
            documentFields.put("filename", Map.of("stringValue", imageFilename));
            documentFields.put("content", Map.of("stringValue", textContent));
            documentFields.put("timestamp", Map.of("integerValue", timestamp));

            // Create the full document structure
            Map<String, Object> requestBody = Map.of(
                    "fields", documentFields
            );

            // Convert to JSON
            assert gson != null;
            String jsonPayload = gson.toJson(requestBody);

            // Make the POST request
            String response = post(url, jsonPayload);

            // Verify successful creation
            if (response.isEmpty()) {
                throw new IOException("Empty response from Firestore");
            }

            // Optional: Parse response to verify success
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            if (!jsonResponse.has("name")) {
                throw new IOException("Failed to save history: " + response);
            }

        } catch (JsonSyntaxException e) {
            throw new IOException("Failed to parse Firestore response", e);
        } catch (Exception e) {
            throw new IOException("Failed to save history", e);
        }
    }
    @Override
    public List<String> getHistoryList(String username) {
        String url = FIREBASE_URL + "/users/" + username + "/history/documents";
        List<String> historyList = new ArrayList<>();

        try {
            String jsonResponse = get(url);

            // Add null/empty check for the response
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                throw new RuntimeException("Empty response from Firebase");
            }

            JsonObject response = gson.fromJson(jsonResponse, JsonObject.class);

            // Check if response is null after parsing
            if (response == null) {
                throw new RuntimeException("Failed to parse Firebase response");
            }

            if (response.has("documents")) {
                JsonArray documents = response.getAsJsonArray("documents");

                for (JsonElement docElement : documents) {
                    JsonObject document = docElement.getAsJsonObject();
                    JsonObject fields = document.getAsJsonObject("fields");

                    String filename = fields.getAsJsonObject("filename")
                            .get("stringValue").getAsString();

                    String timestamp = "unknown";
                    if (fields.has("timestamp")) {
                        JsonObject timestampObj = fields.getAsJsonObject("timestamp");
                        if (timestampObj.has("integerValue")) {
                            timestamp = timestampObj.get("integerValue").getAsString();
                        } else if (timestampObj.has("timestampValue")) {
                            timestamp = timestampObj.get("timestampValue").getAsString();
                        }
                    }

                    historyList.add(filename + " (" + timestamp + ")");
                }
            }
        } catch (Exception e) {
            // Log the error and return empty list
            System.err.println("Error fetching history: " + e.getMessage());
            e.printStackTrace();
        }

        return historyList;
    }

    @Override
    public String getHistoryItem(String username, String historyId) throws IOException {
        String url = FIREBASE_URL + "/users/" + username + "/history/" + historyId;

        try {
            String jsonResponse = get(url);
            assert gson != null;
            JsonObject historyDoc = gson.fromJson(jsonResponse, JsonObject.class);

            // Safely navigate the JSON structure
            if (!historyDoc.has("fields")) {
                return null;
            }

            JsonObject fields = historyDoc.getAsJsonObject("fields");
            if (!fields.has("content")) {
                return null;
            }

            JsonObject contentField = fields.getAsJsonObject("content");
            if (!contentField.has("stringValue")) {
                return null;
            }

            return contentField.get("stringValue").getAsString();

        } catch (JsonSyntaxException e) {
            throw new IOException("Failed to parse history item JSON", e);
        } catch (IllegalStateException e) {
            throw new IOException("Invalid history item data structure", e);
        }
    }
} 