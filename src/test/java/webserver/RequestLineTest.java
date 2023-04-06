package webserver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestLineTest {
    @Test
    public void createMethod(){
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        assertEquals("GET", requestLine.getMethod());
        assertEquals("/index.html", requestLine.getPath());

        requestLine = new RequestLine("POST /index.html HTTP/1.1");
        assertEquals("POST", requestLine.getMethod());
        assertEquals("/index.html", requestLine.getPath());
    }
    @Test
    public void createPathParams(){
        RequestLine requestLine = new RequestLine("GET /user/signup?userId=testID&password=testPwd&name=testName&email=test@test.com HTTP/1.1");
        assertEquals("GET", requestLine.getMethod());
        assertEquals("testID", requestLine.getParams("userId"));
        assertEquals("testPwd", requestLine.getParams("password"));
        assertEquals("testName", requestLine.getParams("name"));
        assertEquals("test@test.com", requestLine.getParams("email"));

    }
}