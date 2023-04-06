package HttpRequestTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpResponseTest {
    private String testRootPath = "./src/test/resource/";
    private String filename;

    @Test
    @DisplayName("forward test")
    public void forwardTest() throws Exception {
        filename = "forward.txt";
        HttpResponse response = new HttpResponse(new File(testRootPath+filename));
        response.forward("/index.html");
    }

    @Test
    @DisplayName("redirect test")
    public void redirectTest() throws Exception {
        filename = "redirect.txt";
        HttpResponse response = new HttpResponse(new File(testRootPath+filename));
        response.redirect("/index.html");
    }

    @Test
    @DisplayName("cookie test")
    public void cookieTest () throws  Exception{
        filename = "cookie.txt";
        HttpResponse response = new HttpResponse(new File(testRootPath+filename));
        response.addHeader("Set-Cookie", "logined=true");
        response.redirect("/index.html");
    }
}
