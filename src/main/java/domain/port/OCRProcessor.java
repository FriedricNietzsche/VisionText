package domain.port;

import java.io.File;

public interface OCRProcessor {
    String extractTextFromImage(File imageFile) throws Exception;
}
