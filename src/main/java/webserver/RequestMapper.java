package webserver;

import webserver.CustomController.*;

import java.util.HashMap;
import java.util.Map;

import static webserver.constant.Http.*;

public class RequestMapper {

    private static final Map<String, Controller> handlerMappingMap = new HashMap<>();

    private RequestMapper() {
    }

    static {
        handlerMappingMap.put(INDEX.getValue(), new ForwardController());
        handlerMappingMap.put("/", new ForwardController());
        handlerMappingMap.put(SIGNUP.getValue(), new SignUpController());
        handlerMappingMap.put(LOGIN.getValue(), new LoginController());
        handlerMappingMap.put(LIST.getValue(), new UserListController());
    }

    public static Controller getController(String path) {
        return handlerMappingMap.get(path);
    }

}
