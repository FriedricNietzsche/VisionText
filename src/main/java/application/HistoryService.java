package application;

import domain.port.HistoryRepository;

import java.io.IOException;
import java.util.List;

public class HistoryService {
    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void saveHistory(String username, String imageFilename, String textContent, long timestamp) throws IOException {
        historyRepository.saveHistory(username, imageFilename, textContent, timestamp);
    }

    public List<String> getHistoryList(String username) {
        return historyRepository.getHistoryList(username);
    }

    public String getHistoryItem(String username, String historyId) throws IOException {
        return historyRepository.getHistoryItem(username, historyId);
    }
}
