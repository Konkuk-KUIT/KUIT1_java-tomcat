package webserver.CustomHandler;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

import static webserver.constant.Http.*;

public class CssHandler implements Controller {

    @Override
    public byte[] process(HttpRequest request, HttpResponse response) throws IOException {
        response.setHeader(CONTENT_TYPE.getValue(), TEXT_CSS.getValue());
        return "Content-Type".getBytes();
    }
}
