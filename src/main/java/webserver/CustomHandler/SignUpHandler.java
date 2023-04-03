package webserver.CustomHandler;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

public class SignUpHandler implements CustomHandler {

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

        setStatusCodeAndLocation("/index.html", response);

        return "/index.html".getBytes();
    }

    private static void setStatusCodeAndLocation(String location, HttpResponse response) {
        response.setStatusCode("302");
        response.setHeader("location", location);
    }
}
