package domain.port;

import java.util.List;

public interface HistoryRepository {
    void saveHistory(String username, String imageFilename, String textContent, long timestamp);
    List<String> getHistoryList(String username);
    String getHistoryItem(String username, String historyId);
}
