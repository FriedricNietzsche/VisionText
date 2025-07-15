package application;

import domain.port.HistoryRepository;
import java.util.List;

public class HistoryService {
    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void saveHistory(String username, String imageFilename, String textContent, long timestamp) {
        historyRepository.saveHistory(username, imageFilename, textContent, timestamp);
    }

    public List<String> getHistoryList(String username) {
        return historyRepository.getHistoryList(username);
    }

    public String getHistoryItem(String username, String historyId) {
        return historyRepository.getHistoryItem(username, historyId);
    }
}
