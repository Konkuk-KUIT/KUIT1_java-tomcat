package http.response;

import http.constant.HttpStatus;
import http.request.HttpRequestStartLine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpResponseStartLineTest {
    HttpResponseStartLine startLine = new HttpResponseStartLine("HTTP1.1");

    @Test
    void codeToMsg() {
        assertEquals("OK", startLine.codeToMsg(HttpStatus.OK));
        assertEquals("Found", startLine.codeToMsg(HttpStatus.REDIRECT));
    }

    @Test
    void validateCode() {
        assertThrows(IllegalArgumentException.class,() -> HttpStatus.getHttpStatus(100));
    }
}
