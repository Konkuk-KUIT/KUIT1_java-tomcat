package webserver.CustomController;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;

import static webserver.constant.Http.*;

public class LoginController implements Controller {

    private final MemoryUserRepository repository;

    public LoginController() {
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) throws IOException {
        String paramUserId = request.getParamValue("userId");
        String paramPassword = request.getParamValue("password");

        User findById = repository.findUserById(paramUserId);
        if (findById == null) {
            response.redirect(USER_LOGIN_FAILED.getValue());
            return;
        }
        passwordCheck(paramPassword, findById, response);
    }

    private void passwordCheck(String paramPassword, User findById, HttpResponse response) throws IOException {
        if (!findById.getPassword().equals(paramPassword)) {
            response.forward(USER_LOGIN_FAILED.getValue());
            return;
        }
        response.addHeader(SET_COOKIE.getValue(), LOGINED_TRUE.getValue());
        response.redirect(INDEX.getValue());
    }
}
