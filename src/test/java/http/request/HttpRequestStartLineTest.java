package http.request;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpRequestStartLineTest {

    @Test
    void getStartLine() throws IOException {
        String line = "GET / HTTP1.1";
        InputStream is = new ByteArrayInputStream(line.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        HttpRequestStartLine startLine = HttpRequestStartLine.from(br);

        assertEquals("GET", startLine.getMethod());
        assertEquals("/", startLine.getPath());
    }

    @Test
    void validateMethod() {
        String line = "PATCH / HTTP1.1";
        InputStream is = new ByteArrayInputStream(line.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        assertThrows(IllegalArgumentException.class,() -> HttpRequestStartLine.from(br));
    }
}
