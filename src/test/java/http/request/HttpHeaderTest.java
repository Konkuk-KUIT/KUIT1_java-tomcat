package http.request;

import http.constants.HttpHeader;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpHeadersTest {
    private final String testDirectory = "./src/test/resources/";
    private final String headerPath = "HttpHeader.txt";

    @Test
    void header() throws IOException {
        HttpHeaders httpHeaders = HttpHeaders.from(bufferedReaderFromFile(testDirectory + headerPath));

        assertEquals("keep-alive",httpHeaders.get(HttpHeader.CONNECTION));
        assertEquals("localhost",httpHeaders.get(HttpHeader.HOST));
        assertEquals("46",httpHeaders.get(HttpHeader.CONTENT_LENGTH));

    }

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }
}