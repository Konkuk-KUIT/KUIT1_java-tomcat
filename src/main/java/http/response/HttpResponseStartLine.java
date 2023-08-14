package http.response;

import http.constant.HttpStatus;

public class HttpResponseStartLine {
    private String version;
    private HttpStatus statusCode;
    private String message;

    public HttpResponseStartLine(String version) {
        this.version = version;
        this.statusCode = HttpStatus.OK;
        this.message = codeToMsg(statusCode);
    }

    static String codeToMsg(HttpStatus status) {
        if (status == HttpStatus.REDIRECT) {
            return "Found";
        }
        return "OK";
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getVersion() + " " + getStatusCode().getStatus() + " " + getMessage() + " \r\n";
    }
}
