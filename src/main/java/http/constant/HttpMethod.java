package http.constant;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"), POST("POST");

    private String method;

    HttpMethod(String method){
        this.method = method;
    }


    public static HttpMethod of(String method) {
        return Arrays.stream(values())
                .filter(m -> method.equals(m.method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("해당되는 Http Method가 없습니다.")));
    }

    public String getMethod() {
        return method;
    }
}
