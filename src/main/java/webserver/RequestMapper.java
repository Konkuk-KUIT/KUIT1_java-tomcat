package webserver;

import Controller.*;
import http.request.HttpRequest;
import http.request.RequestURL;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


//각 컨트롤러를 map에 저장해주는 클래스
public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;

    private static final Map<String, Controller> controllers = new HashMap<>();
    private final Controller controller;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        controller = controllers.get(httpRequest.getUrl());
    }

    //static으로 선언해서 클래스 자체로 관리 -> 싱글톤
    static {
        controllers.put(RequestURL.SIGNUP.getUrl(),new SignUpController());
        controllers.put(RequestURL.LOGIN_POST.getUrl(), new LoginController());
        controllers.put(RequestURL.USER_LIST.getUrl(), new ListController());
        controllers.put(RequestURL.INDEX.getUrl(), new HomeController());
        controllers.put("/",new HomeController());
    }

    //* controller가 null이 아닐 시 실행
    public void proceed() throws IOException {
        if (controller != null) {
            controller.execute(httpRequest, httpResponse);
            return;
        }
        httpResponse.forward(httpRequest.getUrl());
    }

}
