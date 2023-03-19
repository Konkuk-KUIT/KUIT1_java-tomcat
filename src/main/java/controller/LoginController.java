package controller;

import db.MemoryUserRepository;
import http.constants.HttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.request.RequestURL.INDEX;
import static http.request.RequestURL.LOGIN_FAILED;

public class LoginController implements Controller{

    private final MemoryUserRepository repository = MemoryUserRepository.getInstance();
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> queryParameter = request.getQueryParametersFromBody();
        User user = repository.findUserById(queryParameter.get("userId"));
        login(response, queryParameter, user);
    }

    private void login(HttpResponse response, Map<String, String> queryParameter, User user) throws IOException {
        if (user != null && user.getPassword().equals(queryParameter.get("password"))) {
            response.put(HttpHeader.SET_COOKIE,"logined=true");
            response.redirect(INDEX.getUrl());
            return;
        }
        response.redirect(LOGIN_FAILED.getUrl());
    }
}
