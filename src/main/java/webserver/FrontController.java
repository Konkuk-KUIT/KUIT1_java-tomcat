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
        handlerMappingMap.put(SIGNUP.getValue(), new SignUpHandler());
        handlerMappingMap.put(LOGIN.getValue(), new LoginHandler());
        handlerMappingMap.put(LIST.getValue(), new UserListHandler());
    }

    public void service(HttpRequest request, HttpResponse response) throws IOException {
        getController(request).process(request, response);
    }

    private Controller getController(HttpRequest request) {
        Controller controller = handlerMappingMap.get(request.getRequestUri());
        if (controller == null) {
            return new ForwardController();
        }
        return controller;
    }
}
