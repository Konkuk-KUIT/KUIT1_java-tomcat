package webserver.CustomHandler;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

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
            setStatusCodeAndLocation("/user/login_failed.html", response);
            return "/user/login_failed.html".getBytes();
        }
    }

    private static byte[] passwordCheck(Map<String, String> paramMap, User findById, HttpResponse response) {
        if (!findById.getPassword().equals(paramMap.get("password"))) {
            return "/user/login_failed.html".getBytes();
        }
        setStatusCodeAndLocation("/index.html", response);
        response.setHeader("Set-Cookie", "logined=true");
        return "/index.html".getBytes();
    }

    private static void setStatusCodeAndLocation(String location, HttpResponse response) {
        response.setStatusCode("302");
        response.setHeader("location", location);
    }
}
