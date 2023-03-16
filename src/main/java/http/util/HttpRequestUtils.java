package http.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    public static Map<String, String> getQueryParameter(String queryString) {
        String[] queryStrings = queryString.split("&");
        return Arrays.stream(queryStrings)
                .map(q -> q.split("="))
                .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
    }
}