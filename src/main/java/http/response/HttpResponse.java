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

// Http 응답 패킷
// 데이터를 담아 클라이언트에 반환해주는 역할
public class HttpResponse {
    //응답 첫줄 생성
    private final HttpResponseStartLine startLine = new HttpResponseStartLine();
    //Header 정보
    private HttpHeaders httpHeaders;
    private byte[] body;
    private final OutputStream os;

    //* 생성자
    // dos만 담아줄 시 html만 읽어올 수 있도록 설정
    public HttpResponse(OutputStream outputStream) {
        this.os = outputStream;
        this.body = new byte[0];
        httpHeaders = new HttpHeaders(new HashMap<>());
        httpHeaders.put(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
    }

    //http 헤더에 key, value 형태로 데이터 추가
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

    //* path 경로의 데이터를 읽고 HTML이면 html로, css면 css로 읽고 OutputStream에 적어주는 함수
    public void forward(String path) throws IOException {
        setBody(path);
        if (isHtml(path)) {
            write();
            return;
        }
        put(HttpHeader.CONTENT_TYPE,"text/css");
        write();
    }

    //redirect일 시 반환해주는 파일 경로를 같이 설정해서 OutputStream에 적어주는 함수
    public void redirect(String path) throws IOException {
        startLine.setHttpStatus(HttpStatus.REDIRECT);
        startLine.setStatusCode("302");
        put(HttpHeader.LOCATION, path);
        write();
    }


    private boolean isHtml(String path) {
        String[] paths = path.split("\\.");
        return paths[paths.length-1].equals("html");
    }
}
