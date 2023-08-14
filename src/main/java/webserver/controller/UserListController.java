package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.urls.urls;

public class UserListController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.redirect(urls.USER_LIST_HTML.getUrl());
    }
}
