package webserver;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;

public class RequestLine {
    private static final Logger logger = Logger.getLogger(RequestLine.class.getName());
    private String method;
    private String path;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split(" ");
        method = tokens[0];
        if (method.equals("POST")) {
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

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getParams(String key) {
        return params.get(key);
    }
}
