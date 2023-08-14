package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.urls.urls;

public class HomeController implements Controller{
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.forward(urls.INDEX.getUrl());
    }
}
