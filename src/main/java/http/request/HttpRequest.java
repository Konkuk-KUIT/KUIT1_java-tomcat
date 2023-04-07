package http.request;

import http.constants.HttpHeader;
import http.constants.HttpMethod;
import http.util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import static http.constants.HttpHeader.CONTENT_LENGTH;
import static http.util.IOUtils.readData;

//Request 패킷 클래스
public class HttpRequest {
    //첫 라인 정보
    private final HttpRequestStartLine startLine;
    //헤더 정보
    private final HttpHeaders headers;
    //body 정보
    private final String body;

    //* 생성자
    private HttpRequest(final HttpRequestStartLine startLine, final HttpHeaders headers, final String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    //* HttpRequest를 생성 해주는 함수
    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        // 첫 줄 읽어오고
        final String startLine = bufferedReader.readLine();
        // 첫 줄이 없으면 패킷 없음으로 간주 -> 에러 발생
        if (startLine == null) {
            throw new IllegalArgumentException("request가 비어있습니다.");
        }

        //첫줄 읽어오고
        final HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(startLine);
        //br로부터 headers 읽어오고
        final HttpHeaders headers = HttpHeaders.from(bufferedReader);
        //Body 읽어오고
        final String body = readBody(bufferedReader, headers);

        //그 값들을 토대로 HttpRequest 객체 생성
        return new HttpRequest(httpRequestStartLine, headers, body);
    }

    //* Body 읽어오는 함수
    // Body가 없을 시 "" 반환
    private static String readBody(final BufferedReader bufferedReader,
                                   final HttpHeaders headers) throws IOException {

        // Body가 없을 시 "" 반환
        if (!headers.contains(CONTENT_LENGTH)) {
            return "";
        }

        //Body가 있을 시 길이만큼 데이터 읽어오고 반환
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