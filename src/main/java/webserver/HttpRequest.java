package webserver;

import http.util.IOUtils;

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
    private String path;
    private String method;
    private RequestLine requestLine;
    private static final Logger logger = Logger.getLogger(HttpRequest.class.getName());
    // header 정보와 parameter 들은 hashmap 으료 관리
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();


    public HttpRequest(InputStream input) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String line = br.readLine();
            requestLine = new RequestLine(line);

            line = br.readLine();
            while (!line.equals("")) {
                String[] tokens = line.split(":");
                headers.put(tokens[0].trim(), tokens[1].trim()); // 공백을 제거하여 넣어줌
                line = br.readLine();
            }

            if (method.equals("POST")) {
                /*
                POST /test HTTP/1.1
                Host: foo.example
                Content-Type: application/x-www-form-urlencoded
                Content-Length: 27

                field1=value1&field2=value2
                 */
                String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                params = parseQueryParameter(body);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
    public String getMethod() {
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
}
