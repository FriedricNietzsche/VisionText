package application;

import domain.port.HistoryRepository;

import java.io.IOException;
import java.util.List;

public class HistoryService {
    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void saveHistory(String username,
                            String imageFilename,
                            String textContent,
                            long timestamp) throws IOException {
        historyRepository.saveHistory(username, imageFilename, textContent, timestamp);
    }

    public void addHistory(String username, String textContent) throws IOException {
        if (textContent == null || textContent.isBlank()) return;
        historyRepository.saveHistory(username, "", textContent, System.currentTimeMillis());
    }

    public void addHistory(String username, String imageFilename, String textContent) throws IOException {
        if (textContent == null || textContent.isBlank()) return;
        historyRepository.saveHistory(
                username,
                imageFilename == null ? "" : imageFilename,
                textContent,
                System.currentTimeMillis()
        );
    }

    public List<String> getHistoryList(String username) {
        return historyRepository.getHistoryList(username);
    }

    public String getHistoryItem(String username, String historyId) throws IOException {
        return historyRepository.getHistoryItem(username, historyId);
    }

    public void deleteHistory(String username, String historyId) throws IOException {
        if (historyId == null || historyId.isBlank()) {
            throw new IllegalArgumentException("historyId must not be null/blank");
        }
        historyRepository.deleteHistory(username, historyId);
    }
}
