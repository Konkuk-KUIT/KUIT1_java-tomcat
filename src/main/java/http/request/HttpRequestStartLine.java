package http.request;

import http.constant.HttpMethod;
import http.util.HttpRequestUtils;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static http.constant.URL.MAIN_PATH;

public class HttpRequestStartLine {

    private static Map<String, String> queries;
    private HttpMethod method;
    private String path;
    private String version;

    private final static String SEPARATOR = " ";
    private final static String QUERY_IDENTIFIER = "\\?";
    private final static String QUERY_VALUE_IDENTIFIER = "=";

    private HttpRequestStartLine(HttpMethod method, String path, Map<String, String> queries, String version){
        this.method = method;
        this.path = path;
        this.queries = queries;
        this.version = version;
    }

    public static HttpRequestStartLine from(String line){
        String[] startLines = line.split(SEPARATOR);
        if(startLines.length < 3)
            throw new IllegalArgumentException("Request Message의 StartLine의 인자가 잘못되었습니다.");
        HttpMethod method = HttpMethod.of(startLines[0].strip());
        String path = startLines[1].strip();
        String version = startLines[2].strip();

        // query 미존재
        // query가 존재하지 않고, url은 "/"인 경우
        if(path.equals("/"))
            path = MAIN_PATH.getURL();

        if(!path.contains(QUERY_IDENTIFIER)){
            return new HttpRequestStartLine(method, path, null, version);
        }

        // query 존재
        String query = null;
        // query 존재
        if(method == HttpMethod.GET) {
            query = path.split(QUERY_IDENTIFIER)[1];
            path = path.split(QUERY_IDENTIFIER)[0];
        }

        if(path.equals("/"))
            path = MAIN_PATH.getURL();

        queries = HttpRequestUtils.parseQueryParameter(query);

        return new HttpRequestStartLine(method, path, queries, version);
    }



    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public String toString(){
        return method + SEPARATOR + path + SEPARATOR + version + "\r\n";
    }

}
