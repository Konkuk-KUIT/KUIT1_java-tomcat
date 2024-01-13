package webserver.CustomController;

import http.request.HttpRequest;
import http.response.HttpResponse;
import webserver.constant.Http;

import java.io.IOException;

public class ForwardController implements Controller {


    @Override
    public void process(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getRequestUri().equals("/")) {
            response.forward(Http.INDEX.getValue());
            return;
        }
        response.forward(request.getRequestUri());
    }
}
