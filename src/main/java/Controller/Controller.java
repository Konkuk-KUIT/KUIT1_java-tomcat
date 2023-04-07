package Controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;


//* Controller 인터페이스
public interface Controller {
    void execute(HttpRequest request, HttpResponse response) throws IOException;
}
