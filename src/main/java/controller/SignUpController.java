package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequest;
import http.util.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static enumclass.EnumUserQueryKey.*;
import static enumclass.EnumUserQueryKey.EMAIL;
import static http.util.HttpRequestUtils.parseQueryParameter;

public class SignUpController implements Controller{
    private Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if(httpRequest.getMethod().equals("GET")){
            Map<String, String> queryParameter = parseQueryParameter(httpRequest.getURL().split("\\?")[1]);
            User user = new User(queryParameter.get(USERID.getKey()), queryParameter.get(PASSWORD.getKey()), queryParameter.get(NAME.getKey()), queryParameter.get(EMAIL.getKey()));
            repository.addUser(user);
            httpResponse.redirect(HOME_URL, false);
            return;
        }
        String queryString = httpRequest.getBody();
        Map<String, String> queryParameter = parseQueryParameter(queryString);
        User user = new User(queryParameter.get(USERID.getKey()), queryParameter.get(PASSWORD.getKey()), queryParameter.get(NAME.getKey()), queryParameter.get(EMAIL.getKey()));
        repository.addUser(user);
        httpResponse.redirect(HOME_URL, false);
    }
}
