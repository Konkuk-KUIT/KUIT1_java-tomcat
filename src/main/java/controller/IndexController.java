package controller;

import http.constant.RequestUrl;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public class IndexController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(RequestUrl.INDEX.getUrl());
    }
}
