package Controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;
import model.constants.UserQueryKey;

import java.io.IOException;
import java.util.Map;

import static http.request.RequestURL.INDEX;

public class SignUpController implements Controller{

    private final MemoryUserRepository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> queryParameter = request.getQueryParametersFromBody();
        User user = new User(
                queryParameter.get(UserQueryKey.ID.getKey()),
                queryParameter.get(UserQueryKey.PASSWORD.getKey()),
                queryParameter.get(UserQueryKey.NAME.getKey()),
                queryParameter.get(UserQueryKey.EMAIL.getKey()));
        repository.addUser(user);
        response.redirect(INDEX.getUrl());
    }
}
