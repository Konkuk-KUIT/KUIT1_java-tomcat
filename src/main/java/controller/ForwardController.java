package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public class ForwardController implements Controller{
//경로 지정
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(request.getUrl());
    }
}
