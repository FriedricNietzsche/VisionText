package domain.entity;

/**
 * Represents a record containing extracted text from an image.
 * <p>
 * This class is immutable and safe for concurrent use.
 * </p>
 */
public final class TextRecord {
    private final String id;
    private final String imageFilename;
    private final String textContent;
    private final long timestamp;

    /**
     * Constructs a new {@code TextRecord}.
     *
     * @param id            the unique identifier of the record
     * @param imageFilename the filename of the source image
     * @param textContent   the extracted text content
     * @param timestamp     the timestamp of record creation, in milliseconds since epoch
     */
    public TextRecord(String id, String imageFilename, String textContent, long timestamp) {
        this.id = id;
        this.imageFilename = imageFilename;
        this.textContent = textContent;
        this.timestamp = timestamp;
    }

    /**
     * Gets the record ID.
     *
     * @return the record ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the image filename associated with this record.
     *
     * @return the image filename
     */
    public String getImageFilename() {
        return imageFilename;
    }

    /**
     * Gets the extracted text content.
     *
     * @return the text content
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * Gets the creation timestamp of this record.
     *
     * @return the timestamp in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }
}
