package http.util.constant;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"), CONTENT_LENGTH("Content-Length"), SET_COOKIE("Set-Cookie"), COOKIE("Cookie"), LOCATION("Location");

    private String header;

    HttpHeader(String header){
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}
