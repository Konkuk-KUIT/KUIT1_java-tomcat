package http.constant;

public enum HttpMethod {
    GET("GET"), POST("POST");

    private String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod getHttpMethod(String method) {
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        return httpMethod;
    }

    public String getMethod() {
        return method;
    }
}
