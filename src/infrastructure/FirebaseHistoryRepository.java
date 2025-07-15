package infrastructure;

import domain.port.HistoryRepository;
import java.util.List;

public class FirebaseHistoryRepository implements HistoryRepository {
    @Override
    public void saveHistory(String username, String imageFilename, String textContent, long timestamp) {
        // TODO: Implement Firestore save via REST or Admin SDK
    }

    @Override
    public List<String> getHistoryList(String username) {
        // TODO: Return list of history item IDs or summaries for user
        return null;
    }

    @Override
    public String getHistoryItem(String username, String historyId) {
        // TODO: Return text content for a specific history item
        return null;
    }
} 