package application;

import domain.port.OCRProcessor;
import java.io.File;

public class OCRUseCase {
    private final OCRProcessor ocrProcessor;

    public OCRUseCase(OCRProcessor ocrProcessor) {
        this.ocrProcessor = ocrProcessor;
    }

    /** Existing API */
    public String extractText(File imageFile) throws Exception {
        return ocrProcessor.extractTextFromImage(imageFile);
    }

    /** Convenience alias used by UI panels */
    public String runOCR(File imageFile) throws Exception {
        return extractText(imageFile);
    }
}

