package webserver.CustomHandler;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static webserver.constant.Http.*;

public class SignUpHandler implements Controller {

    private final MemoryUserRepository repository;

    public SignUpHandler() {
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public byte[] process(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> paramMap = request.getParamMap();

        User user = new User(
                paramMap.get("userId"),
                paramMap.get("password"),
                paramMap.get("name"),
                paramMap.get("email")
        );

        repository.addUser(user);

        setStatusCodeAndLocation(INDEX.getValue(), response);

        return INDEX.getValue().getBytes();
    }

    private static void setStatusCodeAndLocation(String location, HttpResponse response) {
        response.setStatusCode(FOUND.getValue());
        response.setHeader(LOCATION.getValue(), location);
    }
}
