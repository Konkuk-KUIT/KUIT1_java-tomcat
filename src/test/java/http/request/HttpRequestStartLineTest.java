package http.request;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpRequestStartLineTest {
    private String path = "./src/test/java/resources/StartLine.txt";

    @Test
    void getStartLine() throws IOException {
        InputStreamReader reader = new InputStreamReader(Files.newInputStream(Paths.get(path)));
        BufferedReader br = new BufferedReader(reader);
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
