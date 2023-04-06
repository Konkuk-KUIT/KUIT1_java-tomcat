package HttpRequestTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.HttpRequest;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {
    private String testRootPath = "./src/test/resource/";
    private String testGetFileName = "httpGet.txt";
    private String testPostFileName = "httpPost.txt";
    @Test
    @DisplayName("HTTP GET request data 에서 정보를 잘 가져오는 지 확인")
    public void httpGetParsingTest() throws Exception {
        InputStream input = new FileInputStream(new File(testRootPath + testGetFileName));

        HttpRequest request = new HttpRequest(input);

        String method = request.getMethod();
        String path = request.getPath();
        String connection = request.getHeader("Connection");
        String userId = request.getParameter("userId");

        assertEquals("GET", method);
        assertEquals("/user/signup", path);
        assertEquals("keep-alive", connection);
        assertEquals("test", userId);
    }
    public void httpPostParsingTest() throws Exception {
        InputStream input = new FileInputStream(new File(testRootPath + testPostFileName));

        HttpRequest request = new HttpRequest(input);

        String method = request.getMethod();
        String path = request.getPath();
        String connection = request.getHeader("Connection");
        String userId = request.getParameter("userId");

        assertEquals("POST", method);
        assertEquals("/user/login", path);
        assertEquals("keep-alive", connection);
        assertEquals("test", userId);
    }
}
