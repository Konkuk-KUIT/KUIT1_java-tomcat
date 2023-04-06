package http.response;

import http.constant.Status;

public class HttpResponseStartLine {

    // 기본 200
    private final String version = "HTTP/1.1";
    private Status status = Status.OK;
    private static final String DELIMITER = " ";
    private static final String CRLF = "\r\n";

    public HttpResponseStartLine() {}
    public HttpResponseStartLine(Status status) {
        this.status = status;
    }

    public String getHttpVersion() {
        return version;
    }

    public Status getResponseStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setHttpVersion(String version) {
        version = version;
    }

    @Override
    public String toString(){
        return version + DELIMITER + status.getStatus() + CRLF;
    }

}
