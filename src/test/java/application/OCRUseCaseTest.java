package application;

import domain.port.OCRProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OCRUseCaseTest {
    private OCRProcessor ocrProcessor;
    private OCRUseCase ocrUseCase;

    @BeforeEach
    void setUp() {
        ocrProcessor = Mockito.mock(OCRProcessor.class);
        ocrUseCase = new OCRUseCase(ocrProcessor);
    }

    @Test
    void extractTextDelegates() throws Exception {
        File f = new File("test.png");
        when(ocrProcessor.extractTextFromImage(f)).thenReturn("hello");
        assertEquals("hello", ocrUseCase.extractText(f));
        verify(ocrProcessor).extractTextFromImage(f);
    }

    @Test
    void runOcrAliasCallsExtractText() throws Exception {
        File f = new File("test2.png");
        when(ocrProcessor.extractTextFromImage(f)).thenReturn("world");
        assertEquals("world", ocrUseCase.runOcr(f));
        verify(ocrProcessor).extractTextFromImage(f);
    }
}
