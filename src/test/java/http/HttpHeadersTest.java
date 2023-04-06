package http;


import http.constant.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HttpHeadersTest {

    @Test
    @DisplayName("put Header Test")
    public void putHeaderTest(){
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.put(HttpHeader.CONTENT_LENGTH, "50");
        httpHeaders.put(HttpHeader.LOCATION, "/index.html");

        String response = "Location: /index.html" + "\r\n"
                        + "Content-Length: 50" + "\r\n"
                        + "\r\n";

        assertEquals(response, httpHeaders.toString());

    }
}