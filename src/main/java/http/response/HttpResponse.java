package http.response;

import http.constant.HttpHeader;
import http.constant.HttpStatus;
import http.constant.RequestUrl;
import http.request.HttpHeaders;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static http.constant.RequestUrl.ROOT;

public class HttpResponse {
    private DataOutputStream dos;
    private HttpResponseStartLine startLine;
    private HttpHeaders header;
    private byte[] body;

    public HttpResponse(DataOutputStream dos, String version) {
        this.dos = dos;
        startLine = new HttpResponseStartLine(version);
        header = new HttpHeaders(new HashMap<>());
        body = new byte[0];
    }

    public void forward(String path) throws IOException {
        setBody(path);
        setStartLine(HttpStatus.OK);
        setHeaderContentType(path);
        setHeader(HttpHeader.CONTENT_LENGTH.getHeader(), String.valueOf(body.length));

        response();
    }

    public void redirect(String path, boolean isLogin) throws IOException {
        setStartLine(HttpStatus.REDIRECT);
        setHeader(HttpHeader.LOCATION.getHeader(), path);
        setHeaderCookie(isLogin);

        response();
    }

    private void response() throws IOException {
        dos.writeBytes(startLine.getStartLine());
        dos.writeBytes(header.toString() + "\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }

    public void setBody(String path) throws IOException {
        RequestUrl url = RequestUrl.getRequestUrl(path);
        body = Files.readAllBytes(Paths.get(ROOT.getUrl() + url.getUrl()));
    }

    public void setStartLine(HttpStatus code) {
        startLine.setStatusCode(code);
        String msg = startLine.codeToMsg(code);
        startLine.setMessage(msg);
    }

    public void setHeader(String key, String value) {
        header.putHeader(key, value);
    }

    public void setHeaderContentType(String path) {
        if (path.endsWith(".css")) {
            setHeader(HttpHeader.CONTENT_TYPE.getHeader(), "text/css;charset=utf-8");
            return;
        }
        setHeader(HttpHeader.CONTENT_TYPE.getHeader(), "text/html;charset=utf-8");
    }

    public void setHeaderCookie(boolean isLogin) {
        if (isLogin) {
            setHeader(HttpHeader.SET_COOKIE.getHeader(), "logined=true");
        }
    }
}
