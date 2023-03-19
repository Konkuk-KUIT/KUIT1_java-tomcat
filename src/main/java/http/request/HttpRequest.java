package http.request;

import http.constants.HttpHeader;
import http.constants.HttpMethod;
import http.util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import static http.constants.HttpHeader.CONTENT_LENGTH;
import static http.util.IOUtils.readData;

public class HttpRequest {
    private final HttpRequestStartLine startLine;
    private final HttpHeaders headers;
    private final String body;

    private HttpRequest(final HttpRequestStartLine startLine, final HttpHeaders headers, final String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final String startLine = bufferedReader.readLine();
        if (startLine == null) {
            throw new IllegalArgumentException("request가 비어있습니다.");
        }

        final HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(startLine);
        final HttpHeaders headers = HttpHeaders.from(bufferedReader);
        final String body = readBody(bufferedReader, headers);

        return new HttpRequest(httpRequestStartLine, headers, body);
    }

    private static String readBody(final BufferedReader bufferedReader,
                                   final HttpHeaders headers) throws IOException {

        if (!headers.contains(CONTENT_LENGTH)) {
            return "";
        }

        final int contentLength = convertIntFromContentLength(headers.get(CONTENT_LENGTH));
        return readData(bufferedReader, contentLength);
    }

    private static int convertIntFromContentLength(final String contentLength) {
        return Integer.parseInt(contentLength);
    }

    public final String getQueryParameter(String key) {
        return startLine.getQueryParameter(key);
    }

    public final Map<String,String> getQueryParametersFromBody() {
        return HttpRequestUtils.parseQueryParameter(body);
    }

    public String getUrl() {
        if (startLine.getPath().equals("/")) {
            return RequestURL.INDEX.getUrl();
        }
        return startLine.getPath();
    }

    public HttpMethod getMethod() {
        return startLine.getHttpMethod();
    }

    public String getHeader(HttpHeader headerType) {
        return headers.get(headerType);
    }
}