package domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TextRecordTest {
    @Test
    void recordGettersWork() {
        TextRecord r = new TextRecord("1","img.png","text",42L);
        assertEquals("1", r.getId());
        assertEquals("img.png", r.getImageFilename());
        assertEquals("text", r.getTextContent());
        assertEquals(42L, r.getTimestamp());
    }
}
