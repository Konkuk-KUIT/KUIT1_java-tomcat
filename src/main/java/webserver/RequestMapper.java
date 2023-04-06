package webserver;

import controller.*;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static http.constant.URL.*;

public class RequestMapper {

    Map<String, Controller> controllers = new HashMap<>();
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private String url;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;

        url = httpRequest.getPath();
        makeMap();
    }

    private void makeMap() {
        controllers.put(SIGNUP_ACTION.getURL(), new SignUpController());
        controllers.put(LOGIN_ACTION.getURL(), new LoginController());
        controllers.put(USERLIST_ACTION.getURL(), new UserListController());
    }

    public void proceed() throws IOException {
        Controller controller = null;
        if(controllers.containsKey(url))
            controller = controllers.get(url);

        if(url.endsWith(".html") || url.endsWith(".css"))
            controller = new JustGetController();

        if(controller == null)
            throw new IllegalArgumentException("처리할 수 없는 url 입니다.");
        controller.execute(httpRequest, httpResponse);
    }


}
