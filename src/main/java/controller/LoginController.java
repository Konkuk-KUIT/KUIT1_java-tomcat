package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequest;
import http.util.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static enumclass.EnumUserQueryKey.PASSWORD;
import static enumclass.EnumUserQueryKey.USERID;
import static http.util.HttpRequestUtils.parseQueryParameter;

public class LoginController implements Controller{
    private Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String queryString = httpRequest.getBody();
        Map<String, String> queryParameter = parseQueryParameter(queryString);
        String userid = queryParameter.get(USERID.getKey());
        User user = repository.findUserById(userid);
        login(httpResponse, queryParameter, user);
    }

    private void login(HttpResponse httpResponse, Map<String, String> queryParameter, User user) throws IOException {
        if(user != null && queryParameter.get(PASSWORD.getKey()).equals(user.getPassword())){
            httpResponse.redirect(HOME_URL, true);
            return;
        }
        httpResponse.redirect(LOGIN_FAILED_URL, false);
    }
}
