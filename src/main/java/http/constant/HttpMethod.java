package http.constant;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"), POST("POST");

    private String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod getHttpMethod(String method) {
        HttpMethod m =  Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.method.equals(method))
                .findAny()
                .orElse(null);
        validateMethod(m);
        return m;
    }

    public String getMethod() {
        return method;
    }

    private static void validateMethod(HttpMethod httpMethod) {
        if (httpMethod == null) {
            throw new IllegalArgumentException("지원되지 않는 메소드입니다.");
        }
    }
}
