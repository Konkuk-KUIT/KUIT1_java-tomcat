package webserver.CustomHandler;

import db.MemoryUserRepository;
import model.User;

import java.io.IOException;
import java.util.Map;

public class LoginHandler implements CustomHandler{

    private final MemoryUserRepository repository;

    public LoginHandler() {
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public byte[] process(Map<String, String> paramMap) throws IOException {
        try {
            User findById = repository.findUserById(paramMap.get("userId"));
            return passwordCheck(paramMap, findById);
        } catch (NullPointerException e) {
            return "/user/login_failed.html".getBytes();
        }
    }

    private static byte[] passwordCheck(Map<String, String> paramMap, User findById) {
        if (!findById.getPassword().equals(paramMap.get("password"))) {
            return "/user/login_failed.html".getBytes();
        }
        return "/index.html".getBytes();
    }
}
