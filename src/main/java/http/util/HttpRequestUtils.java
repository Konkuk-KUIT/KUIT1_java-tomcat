package http.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    public static Map<String, String> parseQueryParameter(String queryString) {
        try {
            String[] queryStrings = queryString.split("&");

            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static Map<String, String> parseCookie(String cookieHeader) {
        try {
            String[] cookieString = cookieHeader.split(";");

            return Arrays.stream(cookieString)
                    .map(cookie -> cookie.split("="))
                    .collect(Collectors.toMap(cookies -> cookies[0], cookies -> cookies[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
