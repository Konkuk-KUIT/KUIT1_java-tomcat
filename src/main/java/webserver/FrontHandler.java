package webserver;

import webserver.CustomHandler.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static webserver.constant.Http.*;

public class FrontHandler {

    private static final Map<String, Controller> handlerMappingMap = new HashMap<>();
    private static FrontHandler frontHandler;

    private FrontHandler() {
    }

    public static FrontHandler getInstance() {
        if (handlerMappingMap.isEmpty()) {
            frontHandler = new FrontHandler();
            handlerMapping();
            return frontHandler;
        }
        return frontHandler;
    }

    private static void handlerMapping() {
        handlerMappingMap.put(INDEX.getValue(), new IndexHandler());
        handlerMappingMap.put("/", new IndexHandler());
        handlerMappingMap.put(SIGNUP_FORM.getValue(), new SignUpFormHandler());
        handlerMappingMap.put(SIGNUP.getValue(), new SignUpHandler());
        handlerMappingMap.put(LOGIN_FORM.getValue(), new LoginFormHandler());
        handlerMappingMap.put(USER_LOGIN_FAILED.getValue(), new LoginFailFormHandler());
        handlerMappingMap.put(LOGIN.getValue(), new LoginHandler());
        handlerMappingMap.put(USER_LIST.getValue(), new UserListHandler());
        handlerMappingMap.put(CSS.getValue(), new CssHandler());
    }

    public void service(HttpRequest request, HttpResponse response) throws IOException {

        String requestUri = request.getRequestUri();
        Controller handler;
        if (requestUri.contains(CSS.getValue())) handler = handlerMappingMap.get(CSS.getValue());
        else handler = handlerMappingMap.get(request.getRequestUri());

        byte[] bytes = handler.process(request, response);

        response.createStartLine();
        response.createHeader();
        response.responseBody(bytes);

    }
}
