package http.response;


import http.constants.HttpHeader;
import http.constants.HttpStatus;
import http.request.HttpHeaders;
import http.request.RequestURL;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class HttpResponse {
    private final HttpResponseStartLine startLine = new HttpResponseStartLine();
    private HttpHeaders httpHeaders;
    private byte[] body;
    private final OutputStream os;

    public HttpResponse(OutputStream outputStream) {
        this.os = outputStream;
        this.body = new byte[0];
        httpHeaders = new HttpHeaders(new HashMap<>());
        httpHeaders.put(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
    }

    public void put(HttpHeader key, String value) {
        httpHeaders.put(key, value);
    }

    private void setBody(String path) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get(RequestURL.ROOT.getUrl() + path));
        put(HttpHeader.CONTENT_LENGTH, String.valueOf(body.length));
        this.body = body;
    }

    private void write() throws IOException {
        os.write((startLine.getHttpVersion() +" "+startLine.getStatusCode()+" "+startLine.getHttpStatus().getStatus()+"\r\n").getBytes());
        os.write(httpHeaders.toString().getBytes());
        os.write(body);
        os.flush();
        os.close();
    }

    public void forward(String path) throws IOException {
        setBody(path);
        if (isHtml(path)) {
            write();
            return;
        }
        put(HttpHeader.CONTENT_TYPE,"text/css");
        write();
    }
    //css 구분

    public void redirect(String path) throws IOException {
        startLine.setHttpStatus(HttpStatus.REDIRECT);
        startLine.setStatusCode("302");
        put(HttpHeader.LOCATION, path);
        write();
    }
    //302


    private boolean isHtml(String path) {
        String[] paths = path.split("\\.");
        return paths[paths.length-1].equals("html");
    }
}