package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.HttpHeaders;
import http.constant.HttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.constant.URL.LOGIN_FAILED_PATH;
import static http.constant.URL.MAIN_PATH;
import static model.UserQueryKey.*;

public class LoginController implements Controller {
    private final Repository repository;


    public LoginController(){
        this.repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        // query 분석
        Map<String, String> query = httpRequest.getQuery();

        if(query == null || (query != null && query.size() < 2))
            throw new IllegalArgumentException("로그인 정보를 모두 채워주세요.");
        User user = repository.findUserById(query.get(USERID.getKey()));

        HttpHeaders httpHeaders = new HttpHeaders();
        String path = LOGIN_FAILED_PATH.getURL();

        // 로그인 성공
        if(user != null && user.getPassword().equals(query.get(PASSWORD.getKey()))){
            path = MAIN_PATH.getURL();
            httpHeaders.put(HttpHeader.SET_COOKIE, "logined=true");
        }

        // header
        httpHeaders.put(HttpHeader.LOCATION, path);
        httpResponse.setHeaders(httpHeaders);

        // 로그인 성공 - redirect MAIN_PATH
        httpResponse.redirect(path);
    }
}
