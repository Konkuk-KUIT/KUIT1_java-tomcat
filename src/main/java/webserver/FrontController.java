package webserver;

import http.request.HttpRequest;
import http.response.HttpResponse;
import webserver.CustomController.Controller;
import webserver.CustomController.ForwardController;

import java.io.IOException;

public class FrontController {

    private static FrontController frontController;

    private FrontController() {
    }

    public static FrontController getInstance() {
        if (frontController == null) frontController = new FrontController();
        return frontController;
    }

    public void service(HttpRequest request, HttpResponse response) throws IOException {
        getController(request).process(request, response);
    }

    private Controller getController(HttpRequest request) {
        return RequestMapper.getController(request.getRequestUri());
    }
}
