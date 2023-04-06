package controller;

import http.HttpHeaders;
import http.constant.HttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static http.constant.URL.ROOT;

public class JustGetController implements Controller {

    public JustGetController(){
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        // request 분석
        String requestURL = httpRequest.getPath();
        String TYPE = "html";
        if(requestURL.contains(".css"))
            TYPE = "css";

        // 1 startLine - forward 메소드 내 구현

        // 2
        byte[] body = Files.readAllBytes(Paths.get(ROOT.getURL() + httpRequest.getPath()));
        httpResponse.setBody(body);

        // 3
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put(HttpHeader.CONTENT_LENGTH, Integer.toString(body.length));
        httpHeaders.put(HttpHeader.CONTENT_TYPE, "text/"+TYPE+";charset=utf-8");
        httpResponse.setHeaders(httpHeaders);

        // forward 내부에 writeResponse 메소드 존재.
        httpResponse.forward(requestURL);
    }
}
