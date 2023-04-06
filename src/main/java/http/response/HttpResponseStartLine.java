package http.response;

import http.constant.HttpStatus;

public class HttpResponseStartLine {

    // 기본 200
    private final String version = "HTTP/1.1";
    private HttpStatus httpStatus = HttpStatus.OK;
    private static final String DELIMITER = " ";
    private static final String CRLF = "\r\n";

    public HttpResponseStartLine() {}
    public HttpResponseStartLine(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getHttpVersion() {
        return version;
    }

    public HttpStatus getResponseStatus() {
        return httpStatus;
    }

    public void setStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setHttpVersion(String version) {
        version = version;
    }

    @Override
    public String toString(){
        return version + DELIMITER + httpStatus.getStatus() + CRLF;
    }

}
