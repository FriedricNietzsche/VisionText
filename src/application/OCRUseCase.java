package application;

import domain.port.OCRProcessor;
import java.io.File;

public class OCRUseCase {
    private final OCRProcessor ocrProcessor;

    public OCRUseCase(OCRProcessor ocrProcessor) {
        this.ocrProcessor = ocrProcessor;
    }

    public String extractText(File imageFile) throws Exception {
        return ocrProcessor.extractTextFromImage(imageFile);
    }
}
