package http.constants;

import java.util.Arrays;
import java.util.Optional;

public enum HttpMethod {
    GET("GET"),POST("POST"),DELETE("DELETE");

    String method;
    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod getHttpMethod(String requestMethod) {
        Optional<HttpMethod> method = Arrays.stream(HttpMethod.values()).filter(httpMethod -> httpMethod.method.equals(requestMethod)).findFirst();

        if (method.isPresent()) {
            return method.get();
        }
        throw new IllegalArgumentException("지원하지 않는 Http 메소드 입니다.");
    }

    public String getMethod() {
        return method;
    }
}
