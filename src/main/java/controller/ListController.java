package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;

import java.io.IOException;

public class ListController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if(!httpRequest.getCookie().equals("logined=true")){
            httpResponse.redirect(LOGIN_URL, false);
            return;
        }
        httpResponse.forward(ROOT_URL+LIST_URL, HTML_TYPE);
    }
}
