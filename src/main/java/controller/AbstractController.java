package controller;

import http.constants.HttpMethod;
import http.request.HttpRequest;
import http.response.HttpResponse;

public abstract class AbstractController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        if (request.getMethod().equals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        doPost(request, response);
    }

    abstract void doGet(HttpRequest request, HttpResponse response);
    abstract void doPost(HttpRequest request, HttpResponse response);
}
