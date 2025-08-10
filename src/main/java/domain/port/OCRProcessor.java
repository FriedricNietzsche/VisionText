package domain.port;

import java.io.File;

public interface OCRProcessor {

    /**
     * OCR the image.
     * @param imageFile the image
     * @return the text content
     * @throws Exception error
     */
    String extractTextFromImage(File imageFile) throws Exception;
}
