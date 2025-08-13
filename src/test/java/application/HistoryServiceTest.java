package application;

import domain.port.HistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HistoryServiceTest {
    private HistoryRepository repo;
    private HistoryService service;

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(HistoryRepository.class);
        service = new HistoryService(repo);
    }

    @Test
    void saveHistoryDelegatesAllFields() throws IOException {
        service.saveHistory("u", "img.png", "text", 123L);
        verify(repo).saveHistory("u", "img.png", "text", 123L);
    }

    @Test
    void addHistorySkipsBlank() throws IOException {
        service.addHistory("u", "  ");
        verify(repo, never()).saveHistory(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    void addHistoryWithFilenameWritesNow() throws IOException {
        ArgumentCaptor<Long> ts = ArgumentCaptor.forClass(Long.class);
        service.addHistory("u", "img.png", "hello");
        verify(repo).saveHistory(eq("u"), eq("img.png"), eq("hello"), ts.capture());
        assertTrue(ts.getValue() > 0);
    }

    @Test
    void getHistoryListDelegates() {
        when(repo.getHistoryList("u")).thenReturn(List.of("1","2"));
        assertEquals(List.of("1","2"), service.getHistoryList("u"));
    }

    @Test
    void getHistoryItemDelegates() throws IOException {
        when(repo.getHistoryItem("u","id")).thenReturn("data");
        assertEquals("data", service.getHistoryItem("u","id"));
    }

    @Test
    void deleteHistoryValidatesId() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteHistory("u", " "));
    }
}
