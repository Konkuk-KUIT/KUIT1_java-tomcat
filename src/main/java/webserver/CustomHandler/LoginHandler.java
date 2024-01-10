package webserver.CustomHandler;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static webserver.constant.Http.*;

public class LoginHandler implements Controller {

    private final MemoryUserRepository repository;

    public LoginHandler() {
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> paramMap = request.getParamMap();
        User findById = repository.findUserById(paramMap.get("userId"));
        if (findById == null) {
            response.redirect(USER_LOGIN_FAILED.getValue());
            return;
        }
        passwordCheck(paramMap, findById, response);
    }

    private void passwordCheck(Map<String, String> paramMap, User findById, HttpResponse response) throws IOException {
        if (!findById.getPassword().equals(paramMap.get("password"))) {
            response.forward(USER_LOGIN_FAILED.getValue());
            return;
        }
        response.addHeader(SET_COOKIE.getValue(), LOGINED_TRUE.getValue());
        response.redirect(INDEX.getValue());
    }
}
