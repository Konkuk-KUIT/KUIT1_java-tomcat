package http.response;

import http.constants.HttpMethod;
import http.constants.HttpStatus;
import http.util.HttpRequestUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//RESPONSE 첫 라인 클래스
//기본값이
//200 OK (URL) HTTP/1.1로 설정되어 있음.
public class HttpResponseStartLine {

    private HttpStatus httpStatus = HttpStatus.OK;
    private final String httpVersion = "HTTP/1.1";
    private String statusCode = "200";

    public HttpResponseStartLine() {
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
