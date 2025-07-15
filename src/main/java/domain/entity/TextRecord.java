package domain.entity;

public class TextRecord {
    private String id;
    private String imageFilename;
    private String textContent;
    private long timestamp;

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
