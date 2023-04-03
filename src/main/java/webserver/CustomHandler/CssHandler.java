package webserver.CustomHandler;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class CssHandler implements CustomHandler{

    @Override
    public byte[] process(HttpRequest request, HttpResponse response) throws IOException {
        response.setHeader("Content-Type", "text/css;charset=utf-8");
        return "Content-Type".getBytes();
    }
}
