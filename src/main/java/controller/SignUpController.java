package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.HttpHeaders;
import http.constant.HttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.constant.URL.MAIN_PATH;
import static model.UserQueryKey.*;

public class SignUpController implements Controller {

    private final Repository repository = MemoryUserRepository.getInstance();

    public SignUpController(){
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        // request 분석
        String requestURL = httpRequest.getPath();
        System.out.println(requestURL);

        // method == get
        // GET일 땐 starLine의 requestURL에서 query 얻어올 수 있음.
        Map<String, String> query = httpRequest.getQuery();

        if(query == null || (query != null && query.size() < 4))
            throw new IllegalArgumentException("회원가입 정보를 모두 채워주세요.");
        User user = new User(query.get(USERID.getKey()), query.get(PASSWORD.getKey()), query.get(NAME.getKey()), query.get(EMAIL.getKey()));
        repository.addUser(user);

        // 1 startLine - redirect 메소드 내 구현

        // 2 body x

        // 3 headers
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put(HttpHeader.LOCATION, MAIN_PATH.getURL());
        httpResponse.setHeaders(httpHeaders);

        // redirect 내부에 writeResponse 메소드 존재.
        httpResponse.redirect(MAIN_PATH.getURL());
    }
}
