package application;

import java.io.IOException;
import java.util.List;

import domain.port.HistoryRepository;

/**
 * Service for managing user history.
 */
public final class HistoryService {
    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * Saves an OCR request to the user history.
     * @param username the user
     * @param imageFilename the file
     * @param textContent the text output
     * @param timestamp the time of request
     * @throws IOException error
     */
    public void saveHistory(String username,
                            String imageFilename,
                            String textContent,
                            long timestamp) throws IOException {
        historyRepository.saveHistory(username, imageFilename, textContent, timestamp);
    }

    /**
     * Adds an OCR request to the user history.
     * @param username the user
     * @param textContent the test output
     * @throws IOException error
     */
    public void addHistory(String username, String textContent) throws IOException {
        if (textContent != null && !textContent.isBlank()) {
            historyRepository.saveHistory(username, "", textContent, System.currentTimeMillis());
        }
    }

    /**
     * Adds an OCR request to the user history.
     * @param username the user
     * @param imageFilename the file
     * @param textContent the test output
     * @throws IOException error
     */
    public void addHistory(String username, String imageFilename, String textContent) throws IOException {
        if (textContent != null && !textContent.isBlank()) {
            historyRepository.saveHistory(
                    username,
                    imageFilename,
                    textContent,
                    System.currentTimeMillis()
            );
        }
    }

    /**
     * Getter for the user history.
     * @param username the user
     * @return list of historyId Strings
     * @throws RuntimeException error
     */
    public List<String> getHistoryList(String username) {
        return historyRepository.getHistoryList(username);
    }

    /**
     * Getter for item in the user history.
     * @param username the user
     * @param historyId String mapping to the item
     * @return the history item
     * @throws IOException error
     */
    public String getHistoryItem(String username, String historyId) throws IOException {
        return historyRepository.getHistoryItem(username, historyId);
    }

    /**
     * Deletes an item from the user history.
     * @param username the user
     * @param historyId the item
     * @throws IOException error
     * @throws IllegalArgumentException error
     */
    public void deleteHistory(String username, String historyId) throws IOException {
        if (historyId == null || historyId.isBlank()) {
            throw new IllegalArgumentException("historyId must not be null/blank");
        }
        historyRepository.deleteHistory(username, historyId);
    }
}
