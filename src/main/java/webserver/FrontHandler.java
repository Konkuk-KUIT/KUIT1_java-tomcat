package webserver;

import webserver.CustomHandler.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FrontHandler {

    private static final Map<String, CustomHandler> handlerMappingMap = new HashMap<>();
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
        handlerMappingMap.put("/index.html", new IndexHandler());
        handlerMappingMap.put("/", new IndexHandler());
        handlerMappingMap.put("/user/form.html", new SignUpFormHandler());
        handlerMappingMap.put("/user/signup", new SignUpHandler());
        handlerMappingMap.put("/user/login.html", new LoginFormHandler());
        handlerMappingMap.put("/user/login_failed.html", new LoginFailFormHandler());
        handlerMappingMap.put("/user/login", new LoginHandler());
        handlerMappingMap.put("/user/userList", new UserListHandler());
        handlerMappingMap.put("/css", new CssHandler());
    }

    public void service(HttpRequest request, HttpResponse response) throws IOException {

        String requestUri = request.getRequestUri();
        CustomHandler handler;
        if (requestUri.contains("css")) handler = handlerMappingMap.get("/css");
        else handler = handlerMappingMap.get(request.getRequestUri());

        byte[] bytes = handler.process(request, response);

        response.createStartLine();
        response.createHeader();
        response.responseBody(bytes);

    }
}
