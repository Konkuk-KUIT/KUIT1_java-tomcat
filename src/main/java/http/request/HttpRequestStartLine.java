package http.request;

import http.constants.HttpMethod;
import http.util.HttpRequestUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//* HttpRequest 패킷의 첫줄 정보 클래스
public class HttpRequestStartLine {

    //첫 줄의 정보가 3보다 작으면 안됨
    //Method url version 정보가 있는 라인이기 때문
    private static final int START_LINE_MIN_LENGTH = 3;
    //구분자
    private static final String DISCRIMINATOR = " ";
    //쿼리 값 체크를 위한 ? 구분자
    private static final String PARAM_DISCRIMINATOR = "\\?";
    //http Method(GET,POST,DELETE)
    private final HttpMethod httpMethod;
    //URL 경로
    private final String path;
    //query 값
    private final Map<String, String> query;
    private final String version;

    //HttpRequestStartLine 생성자
    private HttpRequestStartLine(HttpMethod httpMethod, String path, Map<String, String> query, String version) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.query = query;
        this.version = version;
    }

    //* startLine 정보를 통해 parse() 반환
    public static HttpRequestStartLine from(String startLine) {
        return parse(startLine);
    }

    //* 나눠주는 함수
    private static HttpRequestStartLine parse(String startLine) {
        // startLine을 " " 기준으로 나눠줌
        List<String> startLineAttributes = Arrays.asList(startLine.split(DISCRIMINATOR));
        //나눠진 list 값이 3개인지 체크
        validateStartLineLength(startLineAttributes);

        //String 타입 HTTP 메서드를 HttpMethod로 전환
        HttpMethod httpMethod = HttpMethod.getHttpMethod(startLineAttributes.get(0));
        //URL에는 GET 방식으로 읽어올 시 쿼리 값이 포함되있을 수 있으므로
        //"?"로 분리해서 parsePaths에 담아줌
        String[] parsePaths = parsePath(startLineAttributes.get(1));
        //읽어온 값중 url 정보만 가져옴
        String path = parsePaths[0];

        //Query 값이 있을 시 Query 값을 Map으로 변환하여 저장
        Map<String, String> parseQuery = new HashMap<>();
        if (parsePaths.length > 1) {
            parseQuery  = HttpRequestUtils.parseQueryParameter(parsePaths[1]);
        }
        //버전 저장
        String version = startLineAttributes.get(2);

        //그 값들로 HttpRequestStartLine 객체 생성해서 반환
        return new HttpRequestStartLine(httpMethod, path, parseQuery, version);
    }

    private static String[] parsePath(String path) {
        return path.split(PARAM_DISCRIMINATOR);
    }


    private static void validateStartLineLength(final List<String> startLineInfos) {
        if (startLineInfos.size() < START_LINE_MIN_LENGTH) {
            throw new IllegalArgumentException("요청 정보가 잘못되었습니다.");
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }


    public String getQueryParameter(String key) {
        return query.get(key);
    }
}
