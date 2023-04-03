package webserver;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpRequest {

    private String httpMethod;
    private String requestUri;
    private int contentLength;
    private Map<String, String> paramMap;
    private Map<String, String> headerMap;

    private HttpRequest() {}

    public static HttpRequest from(BufferedReader br) throws IOException {
        HttpRequest httpRequest = new HttpRequest();
        String startLine = getStartLine(br);
        httpRequest.httpMethod = getHTTPMethod(startLine);
        httpRequest.requestUri = getURIFromRequestTarget(getRequestTarget(startLine));
        httpRequest.contentLength = getRequestContentLength(br);
        httpRequest.paramMap = HttpRequestUtils.parseQueryParameter(
                IOUtils.readData(br, httpRequest.contentLength));
        httpRequest.headerMap = HttpRequestUtils.parseHeader(
                IOUtils.readData(br, httpRequest.contentLength));

        return httpRequest;
    }


    private static int getRequestContentLength(BufferedReader br) throws IOException {
        int requestContentLength = 0;
        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }
        return requestContentLength;
    }

    private static String getHTTPMethod(String startLine) throws IOException {
        String[] split = startLine.split(" ");
        return split[0];
    }

    private static String getStartLine(BufferedReader br) throws IOException {
        return br.readLine();
    }

    private static String getRequestTarget(String startLine) throws IOException {
        String[] split = startLine.split(" ");
        return split[1];
    }

    private static String getURIFromRequestTarget(String RequestTarget) throws IOException {
        String[] split = RequestTarget.split("\\?");
        return split[0];
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public int getContentLength() {
        return contentLength;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}
