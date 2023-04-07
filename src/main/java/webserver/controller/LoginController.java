package webserver.controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.urls.urls;

public class LoginController implements Controller{
    private final MemoryUserRepository repository = MemoryUserRepository.getInstance();
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter("userId");
        User user = repository.findUserById(userId);
        if (user != null && user.getPassword().equals(request.getParameter("password"))) {
            response.addHeader("Set-Cookie", "logined=true");
            response.redirect(urls.INDEX.getUrl());
        }
        response.redirect(urls.LOGIN_FAILED.getUrl());
    }
}
