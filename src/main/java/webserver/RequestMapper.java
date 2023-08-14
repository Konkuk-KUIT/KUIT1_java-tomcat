package webserver;

import controller.*;
import http.constant.RequestUrl;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest request;
    private final HttpResponse response;

    private static final Map<String, Controller> controllers = new HashMap<>();
    private Controller controller;

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;

        controller = controllers.get(request.getUrl());
        if (controller == null) {
            controller = new ForwardController();
        }
    } static {
        controllers.put("/", new IndexController());
        controllers.put(RequestUrl.SIGNUP_URI.getUrl(), new SignupController());
        controllers.put(RequestUrl.LOGIN_URI.getUrl(), new LoginController());
        controllers.put(RequestUrl.LIST_URI.getUrl(), new ListController());
    }

    public void run() throws IOException {
        controller.execute(request, response);
    }
}
