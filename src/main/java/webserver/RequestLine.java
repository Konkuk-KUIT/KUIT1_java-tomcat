package webserver;

import webserver.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static http.util.HttpRequestUtils.parseQueryParameter;

public class RequestLine {
    private final HttpMethod httpMethod;
    private final String path;
    private Map<String, String> params = new HashMap<>();

    public HttpMethod getMethod() {
        return httpMethod;
    }

    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split(" ");
        httpMethod = HttpMethod.valueOf(tokens[0]);
        if (httpMethod == HttpMethod.POST) {
            path = tokens[1];
            return;
        }
        int index = tokens[1].indexOf("?");
        if (index == -1) {
            path = tokens[1];
        } else {
            path = tokens[1].substring(0, index);
            params = parseQueryParameter(tokens[1].substring(index + 1));
        }
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
