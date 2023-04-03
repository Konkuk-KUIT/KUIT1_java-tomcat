package webserver.CustomHandler;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.constant.Http;

import java.io.IOException;
import java.util.Map;

import static webserver.constant.Http.*;

public class LoginHandler implements CustomHandler{

    private final MemoryUserRepository repository;

    public LoginHandler() {
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public byte[] process(HttpRequest request, HttpResponse response) throws IOException {
        try {
            Map<String, String> paramMap = request.getParamMap();
            User findById = repository.findUserById(paramMap.get("userId"));
            return passwordCheck(paramMap, findById, response);
        } catch (NullPointerException e) {
            setStatusCodeAndLocation(USER_LOGIN_FAILED.getValue(), response);
            return USER_LOGIN_FAILED.getValue().getBytes();
        }
    }

    private static byte[] passwordCheck(Map<String, String> paramMap, User findById, HttpResponse response) {
        if (!findById.getPassword().equals(paramMap.get("password"))) {
            return USER_LOGIN_FAILED.getValue().getBytes();
        }
        setStatusCodeAndLocation(INDEX.getValue(), response);
        response.setHeader(SET_COOKIE.getValue(), LOGINED_TRUE.getValue());
        return INDEX.getValue().getBytes();
    }

    private static void setStatusCodeAndLocation(String location, HttpResponse response) {
        response.setStatusCode(FOUND.getValue());
        response.setHeader(LOCATION.getValue(), location);
    }
}
