package http.constant;

import java.util.Arrays;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"), CONTENT_LENGTH("Content-Length"), SET_COOKIE("Set-Cookie"), COOKIE("Cookie"), LOCATION("Location");

    private String header;

    HttpHeader(String header){
        this.header = header;
    }

    public static HttpHeader of(String header) {
        return Arrays.stream(values())
                .filter(h -> header.equals(h.header))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("해당되는 Http Header가 없습니다.")));
    }

    public String getHeader() {
        return header;
    }
}
