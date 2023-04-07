package webserver;

import controller.*;
import db.Repository;
import enumclass.EnumURL;
import http.util.HttpRequest;
import http.util.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static enumclass.EnumMethod.GET;
import static enumclass.EnumMethod.POST;
import static enumclass.EnumURL.*;
import static enumclass.EnumURL.USERLIST;

public class RequestMapper {
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private static Map<String, Controller> controllers = new HashMap<>();

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;

        controllers.put(HTML.getUrl(), new ForwardController());
        controllers.put("/", new HomeController());
        controllers.put(SIGNUP.getUrl(), new SignUpController());
        controllers.put(LOGIN.getUrl(), new LoginController());
        controllers.put(USERLIST.getUrl(), new ListController());
        controllers.put(CSS.getUrl(), new ForwardController());
    }

    public void proceed() throws IOException {
        if(controllers.containsKey(httpRequest.getURL())){
            controllers.get(httpRequest.getURL()).execute(httpRequest, httpResponse);
        }

        if(httpRequest.getURL().endsWith(HTML.getUrl())){
            controllers.get(HTML.getUrl()).execute(httpRequest, httpResponse);
        }

        if(httpRequest.getURL().contains(SIGNUP.getUrl())){
            controllers.get(SIGNUP.getUrl()).execute(httpRequest, httpResponse);
        }

        if(httpRequest.getURL().endsWith(CSS.getUrl())){
            controllers.get(CSS.getUrl()).execute(httpRequest, httpResponse);
        }
    }
}
