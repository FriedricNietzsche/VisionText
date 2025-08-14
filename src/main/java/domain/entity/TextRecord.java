package domain.entity;

/**
 * Represents a text record entity.
 * @null
 */
public final class TextRecord {
    private final String id;
    private final String imageFilename;
    private final String textContent;
    private final long timestamp;

    public TextRecord(String id, String imageFilename, String textContent, long timestamp) {
        this.id = id;
        this.imageFilename = imageFilename;
        this.textContent = textContent;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public String getTextContent() {
        return textContent;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
