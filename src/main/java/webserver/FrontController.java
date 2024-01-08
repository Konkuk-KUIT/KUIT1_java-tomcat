package webserver;

import webserver.CustomHandler.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static webserver.constant.Http.*;

public class FrontController {

    private static final Map<String, Controller> handlerMappingMap = new HashMap<>();
    private static FrontController frontController;

    private FrontController() {
    }

    public static FrontController getInstance() {
        if (handlerMappingMap.isEmpty()) {
            frontController = new FrontController();
            handlerMapping();
            return frontController;
        }
        return frontController;
    }

    private static void handlerMapping() {
        handlerMappingMap.put(INDEX.getValue(), new ForwardController());
        handlerMappingMap.put("/", new ForwardController());
        handlerMappingMap.put(SIGNUP_FORM.getValue(), new ForwardController());
        handlerMappingMap.put(SIGNUP.getValue(), new SignUpHandler());
        handlerMappingMap.put(LOGIN_FORM.getValue(), new ForwardController());
        handlerMappingMap.put(USER_LOGIN_FAILED.getValue(), new ForwardController());
        handlerMappingMap.put(LOGIN.getValue(), new LoginHandler());
        handlerMappingMap.put(USER_LIST.getValue(), new UserListHandler());
        handlerMappingMap.put(CSS.getValue(), new CssHandler());
    }

    public void service(HttpRequest request, HttpResponse response) throws IOException {

        String requestUri = request.getRequestUri();
        Controller Controller;
        if (requestUri.contains(CSS.getValue())) Controller = handlerMappingMap.get(CSS.getValue());
        else Controller = handlerMappingMap.get(request.getRequestUri());

        Controller.process(request, response);
    }
}
