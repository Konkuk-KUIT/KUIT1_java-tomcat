package http.util;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    void httpRequest() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("./src/test/java/resource/test.txt"));
        assertEquals("/user/create", httpRequest.getURL());
    }

}
