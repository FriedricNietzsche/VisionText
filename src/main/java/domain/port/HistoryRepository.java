package domain.port;

import java.io.IOException;
import java.util.List;

public interface HistoryRepository {
    void saveHistory(String username, String imageFilename, String textContent, long timestamp) throws IOException;

    List<String> getHistoryList(String username);

    String getHistoryItem(String username, String historyId) throws IOException;

    /** NEW: delete a history item by its id/key. */
    void deleteHistory(String username, String historyId) throws IOException;
}
