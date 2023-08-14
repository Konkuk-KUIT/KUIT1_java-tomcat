package webserver.controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.urls.urls;

public class SignUpController implements Controller{
    private final MemoryUserRepository repository = MemoryUserRepository.getInstance();
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email")
        );
        repository.addUser(user);
        response.redirect(urls.INDEX.getUrl());
    }
}
