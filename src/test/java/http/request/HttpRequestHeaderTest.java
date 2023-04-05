package http.request;

import http.constant.HttpHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestHeaderTest {
    private final String path = "./src/test/java/resources/HttpHeader.txt";
    private HttpHeaders header;

    @BeforeEach
    void init() throws IOException {
        InputStreamReader reader = new InputStreamReader(Files.newInputStream(Paths.get(path)));
        BufferedReader br = new BufferedReader(reader);
        header = HttpHeaders.from(br);
    }

    @Test
    void hasKey() {
        assertTrue(header.hasKey(HttpHeader.CONTENT_LENGTH));
        assertFalse(header.hasKey(HttpHeader.SET_COOKIE));
    }

    @Test
    void getValue() {
        assertEquals("40", header.getValue(HttpHeader.CONTENT_LENGTH));
        assertEquals(null, header.getValue(HttpHeader.SET_COOKIE));
    }
}
