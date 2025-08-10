package application;

import java.io.File;

import domain.port.OCRProcessor;

public class OCRUseCase {
    private final OCRProcessor ocrProcessor;

    public OCRUseCase(OCRProcessor ocrProcessor) {
        this.ocrProcessor = ocrProcessor;
    }

    /**
     * Existing API.
     * @param imageFile the image
     * @return text output
     * @throws Exception error
     */
    public String extractText(File imageFile) throws Exception {
        return ocrProcessor.extractTextFromImage(imageFile);
    }

    /**
     * Convenience alias used by UI panels.
     * @param imageFile the image
     * @return text output
     * @throws Exception error
     */
    public String runOcr(File imageFile) throws Exception {
        return extractText(imageFile);
    }
}

