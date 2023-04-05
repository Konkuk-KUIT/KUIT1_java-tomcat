package http.request;

import http.constant.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestStartLine {
    private HttpMethod method;
    private String path;
    private String version;

    public HttpRequestStartLine(HttpMethod method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpRequestStartLine from(BufferedReader br) throws IOException {
        String[] lines = br.readLine().split(" ");

        HttpMethod method = HttpMethod.getHttpMethod(lines[0]);
        String url = lines[1];
        String version = lines[2];

        return new HttpRequestStartLine(method, url, version);
    }

    public String getMethod() {
        return method.getMethod();
    }

    public String getPath() {
        return path;
    }
}
