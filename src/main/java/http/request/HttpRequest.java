package http.request;

import http.constant.HttpHeader;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private HttpRequestStartLine startLine;
    private HttpRequestHeader header;
    private String body;

    public HttpRequest(HttpRequestStartLine startLine, HttpRequestHeader header, String body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        HttpRequestStartLine startLine = HttpRequestStartLine.from(br);
        HttpRequestHeader header = HttpRequestHeader.from(br);
        String body = requestBody(br, header);

        return new HttpRequest(startLine, header, body);
    }

    private static String requestBody(BufferedReader br, HttpRequestHeader header) throws IOException {
        if (!header.hasKey(HttpHeader.CONTENT_LENGTH)) {
            return "";
        }

        String length =  header.getValue(HttpHeader.CONTENT_LENGTH);
        int contentLength = Integer.parseInt(length);

        return IOUtils.readData(br, contentLength);
    }

    public String getMethod() {
        return startLine.getMethod();
    }

    public String getUrl() {
        return startLine.getPath();
    }

    public String getBody() {
        return body;
    }

    public boolean hasHeaderKey(HttpHeader key) {
        return header.hasKey(key);
    }

    public String getHeaderValue(HttpHeader key) {
        return header.getValue(key);
    }
}
