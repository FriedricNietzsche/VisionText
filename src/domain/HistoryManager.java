package domain;

import java.util.List;

public class HistoryManager {
    public void saveHistory(String username, String imageFilename, String textContent, long timestamp) {
        // TODO: Implement Firestore save via REST or Admin SDK
    }

    public List<String> getHistoryList(String username) {
        // TODO: Return list of history item IDs or summaries for user
        return null;
    }

    public String getHistoryItem(String username, String historyId) {
        // TODO: Return text content for a specific history item
        return null;
    }
} 