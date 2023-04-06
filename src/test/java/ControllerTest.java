import controller.Controller;
import http.request.HttpRequest;
import http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import controller.JustGetController;

class ControllerTest {


    private final static String testDirectory = "./src/test/java";
    private final static String reqPath = "/res/request/";
    private final static String respPath = "/res/response/";
    private static String reqFile= "GET_message.txt";
    private String respFile = "GET_res.txt";

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    @DisplayName("JustGetController - .html의 body 반환")
    void HTMLControllerTest() throws IOException {
        String responseFile = "GET_index_html.txt";
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + reqPath + reqFile));

        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+ respPath + responseFile));

        Controller htmlController = new JustGetController();
        htmlController.execute(httpRequest, httpResponse);
    }

    @Test
    @DisplayName("JustGetController - .css의 body 반환")
    void CSSControllerTest() throws IOException {
        String responseFile = "Forward_css.txt";
        reqFile = "GET_css.txt";
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + reqPath + reqFile));

        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+ respPath + responseFile));

        Controller cssController = new JustGetController();
        cssController.execute(httpRequest, httpResponse);
    }


}