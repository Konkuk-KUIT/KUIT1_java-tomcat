package http;

import http.request.HttpRequest;
import http.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResponseTest {
    private final static String testDirectory = "./src/test/java";
    private final static String reqPath = "/res/request/";
    private final static String respPath = "/res/response/";
    private static String reqFile= "GET_message.txt";
    private String respFile = "GET_res.txt";

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @BeforeEach
    void init() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + reqPath + reqFile));
    }

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    @DisplayName("httpResponseMessage test - forward() 테스트")
    void forwardTest() throws IOException {
        respFile = "GET_index.txt";
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory + respPath +respFile));
        httpResponse.forward("/index.html");


        respFile = "GET_list.txt";
        httpResponse = new HttpResponse(outputStreamToFile(testDirectory + respPath +respFile));
        httpResponse.forward("/user/list.html");
    }


    @Test
    @DisplayName("url이 \"/\"인 경우 처리 Test")
    void forward_SlashAddress_Test() throws IOException {
        reqFile = "GET_slash.txt";
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + reqPath + reqFile));


        respFile = "GET_slash_resp.txt";
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+ respPath +respFile));
        httpResponse.forward(httpRequest.getPath());
    }

    @Test
    @DisplayName("httpResponseMessage test - check attribute")
    void httpResponseMessageTest() throws IOException {
        respFile = "RED_index.txt";
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+ respPath + respFile));
        httpResponse.redirect("/index.html");


        respFile = "RED_list.txt";
        httpResponse = new HttpResponse(outputStreamToFile(testDirectory+ respPath + respFile));
        httpResponse.redirect("/user/list.html");

    }

    @Test
    @DisplayName("Request 얻어와서 StartLine 생성하기")
    void makeStartLine_fromRequest(){

    }

}
