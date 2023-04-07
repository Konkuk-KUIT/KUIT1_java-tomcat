package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;

import java.io.IOException;

public class HomeController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.forward(ROOT_URL+HOME_URL, HTML_TYPE);
    }
}
