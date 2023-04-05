package http.response;

import http.constant.HttpHeader;
import http.request.HttpHeaders;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class HttpResponseHeaderTest {
    @Test
    void putHeader() {
        HttpHeaders header = new HttpHeaders(new HashMap<>());
        header.putHeader("Content-Length", "12");

        assertTrue(header.hasKey(HttpHeader.CONTENT_LENGTH));
        assertFalse(header.hasKey(HttpHeader.LOCATION));
        assertEquals("12", header.getValue(HttpHeader.CONTENT_LENGTH));
    }
}
