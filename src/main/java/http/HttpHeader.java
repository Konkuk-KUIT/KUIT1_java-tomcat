package http;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"), CONTENT_TYPE("Content-Type"),
    LOCATION("Location"), SET_COOKIE("Set-Cookie"), LOGINED_TRUE("logined=true"),
    TEXT_HTML("text/html;charset=utf-8"), TEXT_CSS("text/css;charset=utf-8");

    private String header;

    HttpHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
