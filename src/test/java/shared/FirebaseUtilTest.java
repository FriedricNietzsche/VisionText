package shared;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FirebaseUtilTest {
    private MockWebServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void postReturnsBodyOn200() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("ok"));
        String url = server.url("/post").toString();
        String resp = FirebaseUtil.post(url, "{\"a\":1}");
        assertEquals("ok", resp);
    }

    @Test
    void getReturnsBodyOn200() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("data"));
        String url = server.url("/get").toString();
        String resp = FirebaseUtil.get(url);
        assertEquals("data", resp);
    }

    @Test
    void deleteReturnsBodyOn200() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("bye"));
        String url = server.url("/delete").toString();
        String resp = FirebaseUtil.delete(url);
        assertEquals("bye", resp);
    }

    @Test
    void postThrowsOnNon200() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500).setBody("err"));
        String url = server.url("/post").toString();
        assertThrows(IOException.class, () -> FirebaseUtil.post(url, "{}"));
    }
}
