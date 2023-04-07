package http.constants;

import java.util.Arrays;
import java.util.Optional;

//Header 정보 Enum 클래스
public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"), CONTENT_TYPE("Content-Type"), LOCATION("Location"), COOKIE("Cookie"),SET_COOKIE("Set-Cookie"),CONNECTION("Connection"),HOST("Host");

    private String header;

    HttpHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    //싱글톤 패턴
    public static HttpHeader getHeaderInstance(String headerString) {
        Optional<HttpHeader> find = Arrays.stream(HttpHeader.values()).filter(httpHeader -> httpHeader.header.equals(headerString)).findFirst();
        return find.orElse(null);
    }
}
