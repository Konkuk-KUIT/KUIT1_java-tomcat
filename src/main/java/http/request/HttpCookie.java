package http.request;

import http.util.HttpRequestUtils;

import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookies;

    HttpCookie(String cookieValue) {
        cookies = HttpRequestUtils.parseCookie(cookieValue);
    }

    public void putCookie(String key, String value) {
        cookies.put(key,value);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
