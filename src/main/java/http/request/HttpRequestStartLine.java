package http.request;

import http.constants.HttpMethod;
import http.util.HttpRequestUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestStartLine {

    private static final int START_LINE_MIN_LENGTH = 3;
    private static final String DISCRIMINATOR = " ";
    private static final String PARAM_DISCRIMINATOR = "\\?";
    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> query;
    private final String version;

    private HttpRequestStartLine(HttpMethod httpMethod, String path, Map<String, String> query, String version) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.query = query;
        this.version = version;
    }

    public static HttpRequestStartLine from(String startLine) {
        return parse(startLine);
    }

    private static HttpRequestStartLine parse(String startLine) {
        List<String> startLineAttributes = Arrays.asList(startLine.split(DISCRIMINATOR));
        validateStartLineLength(startLineAttributes);

        HttpMethod httpMethod = HttpMethod.getHttpMethod(startLineAttributes.get(0));
        String[] parsePaths = parsePath(startLineAttributes.get(1));
        String path = parsePaths[0];

        Map<String, String> parseQuery = new HashMap<>();
        if (parsePaths.length > 1) {
            parseQuery  = HttpRequestUtils.parseQueryParameter(parsePaths[1]);
        }
        String version = startLineAttributes.get(2);
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
