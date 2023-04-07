package webserver;

import http.util.IOUtils;
import webserver.http.HttpMethod;
import webserver.urls.urls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;

public class HttpRequest {
    private RequestLine requestLine;
    private static final Logger logger = Logger.getLogger(HttpRequest.class.getName());
    // header 정보와 parameter 들은 hashmap 으료 관리
    private final Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();


    public HttpRequest(InputStream input) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String line = br.readLine();
            if (line == null) {
                return;
            }
            requestLine = new RequestLine(line);
            line = br.readLine();
            while (!line.equals("")) {
                String[] tokens = line.split(":");
                headers.put(tokens[0].trim(), tokens[1].trim()); // 공백을 제거하여 넣어줌
                line = br.readLine();
            }

            if (requestLine.getMethod().isPost()) {
                String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                params = parseQueryParameter(body);
            } else {
                params = requestLine.getParams();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String key) {
        return params.get(key);
    }

    public String getUrl() {
        if (requestLine.getPath().equals("/")) {
            return urls.INDEX.getUrl();
        }
        return requestLine.getPath();
    }

}
