package http.constant;

import java.util.Arrays;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"), CONTENT_TYPE("Content-Type"),
    LOCATION("Location"), SET_COOKIE("Set-Cookie"), COOKIE("Cookie");

    private String header;

    HttpHeader(String header) {
        this.header = header;
    }

    public static HttpHeader getHttpHeader(String header) {
        return Arrays.stream(HttpHeader.values())
                .filter(httpHeader -> httpHeader.header.equals(header))
                .findAny()
                .orElse(null);
    }

    public String getHeader() {
        return header;
    }
}
