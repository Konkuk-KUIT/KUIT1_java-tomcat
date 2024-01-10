package webserver;

import webserver.constant.Http;

public class HttpResponseStartLine {

    private final String httpVersion = "HTTP/1.1";
    private String statusCode = "200";
    private String status = Http.OK.getValue();

    public HttpResponseStartLine() {
    }

    public void setStatus(Http status) {
        this.status = status.getValue();
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return httpVersion + " " + statusCode + " " + status;
    }
}
