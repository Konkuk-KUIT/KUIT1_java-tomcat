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

public class LoginController implements Controller{
    Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String query = request.getBody();
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(query);
        User user = repository.findUserById(queryParameter.get(UserQueryKey.ID.getQueryKey()));

        // 로그인 성공
        if ((user != null) && user.getPassword().equals(queryParameter.get(UserQueryKey.PASSWORD.getQueryKey()))) {
            response.redirect(RequestUrl.INDEX.getUrl(), true); // set cookie
            return;
        }

        // 로그인 실패
        response.redirect(RequestUrl.LOGIN_FAILED_URL.getUrl(), false);
    }
}
