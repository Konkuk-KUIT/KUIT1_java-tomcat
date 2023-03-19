package http.response;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {
    private final String testDirectory = "./src/test/resources/";
    private final String forwardPath = "Http_Forward.txt";
    private final String responseRedirectPath = "Http_Response_Redirect.txt";

    @Test
    void responseForward() throws IOException {
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+forwardPath));

        httpResponse.forward("/index.html");
    }


    @Test
    void responseRedirect() throws IOException {
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+responseRedirectPath));

        httpResponse.redirect("/index.html");
    }

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

}