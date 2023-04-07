package webserver;

import webserver.controller.*;
import webserver.urls.urls;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static final Map<String, Controller> controllers = new HashMap<>();
    private final Controller controller;
    private final HttpResponse response;
    private final HttpRequest request;

    public RequestMapping(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        controller = controllers.get(request.getUrl());
    }
    static {
        controllers.put(urls.SIGNUP.getUrl(), new SignUpController());
        controllers.put(urls.LOGIN_POST.getUrl(), new LoginController());
        controllers.put(urls.USER_LIST.getUrl(), new UserListController());
        controllers.put(urls.INDEX.getUrl(), new HomeController());
        controllers.put("/", new HomeController());
    }
    public void proceed() throws IOException {
        if (controller != null) {
            controller.service(request, response);
            return;
        }
        response.forward(request.getUrl());
    }
}
