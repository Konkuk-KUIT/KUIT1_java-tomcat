package webserver.CustomHandler;

import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.constant.Http;

import java.io.IOException;

import static webserver.constant.Http.*;

public class CssHandler implements CustomHandler{

    @Override
    public byte[] process(HttpRequest request, HttpResponse response) throws IOException {
        response.setHeader(CONTENT_TYPE.getValue(), TEXT_CSS.getValue());
        return "Content-Type".getBytes();
    }
}
