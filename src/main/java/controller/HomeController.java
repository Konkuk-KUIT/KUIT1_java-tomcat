package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static http.request.RequestURL.INDEX;

public class HomeController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(INDEX.getUrl());
    }
}
