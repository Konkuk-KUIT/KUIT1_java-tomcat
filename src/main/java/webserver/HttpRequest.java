package webserver;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static java.lang.System.Logger.Level.ERROR;

public class HttpRequest {
    private String path;
    private String method;
    private static final Logger logger  = Logger.getLogger(HttpRequest.class.getName());
    // header 정보와 parameter 들은 hashmap 으료 관리
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();


    public HttpRequest(InputStream input) {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String line = br.readLine();
            processRequestLine(line);

            line = br.readLine();
            while(!line.equals("")){
                String[] tokens = line.split(":");
                headers.put(tokens[0].trim(), tokens[1].trim()); // 공백을 제거하여 넣어줌
                line = br.readLine();
            }

            if(method.equals("POST")){
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

    public void processRequestLine(String requestLine){
        String [] tokens = requestLine.split(" ");
        method = tokens[0];
        if(method.equals("POST")){
            path = tokens[1];
            return;
        }
        int index = tokens[1].indexOf("?");
        if(index==-1){
            path = tokens[1];
            return;
        }
        path = tokens[1].substring(0, index);
        params = parseQueryParameter(tokens[1].substring(index+1));
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String key) {
        return params.get(key);
    }
}
