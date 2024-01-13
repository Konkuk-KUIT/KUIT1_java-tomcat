package webserver.CustomHandler;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public interface Controller {

    void process(HttpRequest request, HttpResponse response) throws IOException;
}
