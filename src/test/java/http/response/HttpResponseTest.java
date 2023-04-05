package http.response;

import http.constant.HttpHeader;
import http.constant.HttpStatus;
import http.constant.RequestUrl;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResponseTest {
    @Test
    void forward() throws IOException {
        String path = "./src/test/java/resources/Forward.txt";
        OutputStream os = Files.newOutputStream(Paths.get(path));
        DataOutputStream dos = new DataOutputStream(os);
        HttpResponse response = new HttpResponse(dos, "HTTP1.1");

        response.forward(RequestUrl.INDEX.getUrl());
    }

    @Test
    void redirect() throws IOException {
        String path = "./src/test/java/resources/Redirect.txt";
        OutputStream os = Files.newOutputStream(Paths.get(path));
        DataOutputStream dos = new DataOutputStream(os);
        HttpResponse response = new HttpResponse(dos, "HTTP1.1");

        response.redirect(RequestUrl.LOGIN_URL.getUrl(), false);
    }

}
