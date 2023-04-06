package http.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
class HttpRequestUtilsTest {

    @Test
    void parseQuery() {
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter("userId=1");
        assertEquals("1",queryParameter.get("userId"));
    }

    @Test
    void parseQueryMore() {
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter("userId=1&password=1");
        assertEquals("1",queryParameter.get("userId"));
        assertEquals("1",queryParameter.get("password"));
    }

    @Test
    void parseQueryZero() {
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter("");
    }

    @Test
    void parseCookie() {
        Map<String, String> cookies = HttpRequestUtils.parseCookie("logined=true");
        assertEquals("true",cookies.get("logined"));
    }

    @Test
    void parseCookieMany() {
        Map<String, String> cookies = HttpRequestUtils.parseCookie("logined=true;jessionId=123");
        assertEquals("true",cookies.get("logined"));
        assertEquals("123",cookies.get("jessionId"));

    }
}