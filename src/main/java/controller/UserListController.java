package controller;

import http.HttpHeaders;
import http.constant.HttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import static http.constant.URL.*;

public class UserListController implements Controller {

    public UserListController(){
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        // request
        HttpHeaders headers = httpRequest.getHeader();
        System.out.println(headers.toString());
        String cookie = headers.get(HttpHeader.COOKIE);
        String path = LOGIN_PATH.getURL();

        if(cookie != null && cookie.contains("logined=true")){
            path = USERLIST_PATH.getURL();
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put(HttpHeader.LOCATION, path);
        httpResponse.setHeaders(httpHeaders);

        httpResponse.redirect(path);

    }
}
