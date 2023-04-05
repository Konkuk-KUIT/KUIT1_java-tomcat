package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.constant.RequestUrl;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;
import model.UserQueryKey;

import java.io.IOException;
import java.util.Map;

public class SignupController implements Controller {
    Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String query = request.getBody();
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(query);
        User user = new User(queryParameter.get(UserQueryKey.ID.getQueryKey()),
                queryParameter.get(UserQueryKey.PASSWORD.getQueryKey()),
                queryParameter.get(UserQueryKey.NAME.getQueryKey()),
                queryParameter.get(UserQueryKey.EMAIL.getQueryKey()));
        repository.addUser(user);

        response.redirect(RequestUrl.INDEX.getUrl(), false);
    }
}
