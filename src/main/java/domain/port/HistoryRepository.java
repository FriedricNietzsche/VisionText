package domain.port;

import java.io.IOException;
import java.util.List;

public interface HistoryRepository {

    /**
     * Saves an OCR request to the user history.
     * @param username the user
     * @param imageFilename the file
     * @param textContent the text output
     * @param timestamp the time of request
     * @throws IOException error
     */
    void saveHistory(String username, String imageFilename, String textContent, long timestamp) throws IOException;

    /**
     * Getter for the user history.
     * @param username the user
     * @return list of historyId Strings
     */
    List<String> getHistoryList(String username);

    /**
     * Getter for item in the user history.
     * @param username the user
     * @param historyId String mapping to the item
     * @return the history item
     * @throws IOException error
     */
    String getHistoryItem(String username, String historyId) throws IOException;

    /**
     * Deletes an item from the user history.
     * @param username the user
     * @param historyId the item
     * @throws IOException error
     * @throws IllegalArgumentException error
     */
    void deleteHistory(String username, String historyId) throws IOException;
}
