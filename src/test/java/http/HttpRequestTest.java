package http;

import http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static http.constant.HttpMethod.POST;
import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {


    private final static String testDirectory = "./src/test/java";
    private final static String reqPath = "/res/request/";
    private final static String reqFile= "POST_message.txt";

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }



    @Test
    @DisplayName("httpRequestMessage test - check attribute")
    void httpRequestMessageTest() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + reqPath + reqFile));
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals(POST, httpRequest.getMethod());


    }

}