package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.constant.HttpHeader;
import http.constant.HttpMethod;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import http.constant.RequestUrl;
import model.User;
import model.UserQueryKey;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private static final String ROOT = RequestUrl.ROOT.getUrl();
    private static final String INDEX = RequestUrl.INDEX.getUrl();
    private static final String SIGNUP_URI = RequestUrl.SIGNUP_URI.getUrl();
    private static final String LOGIN_URI = RequestUrl.LOGIN_URI.getUrl();
    private static final String LOGIN_URL = RequestUrl.LOGIN_URL.getUrl();
    private static final String LOGIN_FAILED_URL = RequestUrl.LOGIN_FAILED_URL.getUrl();
    private static final String LIST_URI = RequestUrl.LIST_URI.getUrl();
    private static final String LIST_URL = RequestUrl.LIST_URL.getUrl();


    private final Repository repository;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest request = HttpRequest.from(br);
            HttpResponse response = new HttpResponse(dos, request.getVersion());

            // 요구사항 1: index.html 반환하기
            if (request.getUrl().equals("/")) {
                response.forward(INDEX);
                return;
            }

            if (request.getMethod().equals(HttpMethod.GET.getMethod()) && request.getUrl().endsWith(".html")) {
                response.forward(request.getUrl());
                return;
            }

//            // 요구사항 2: GET 방식으로 회원가입하기
//            if (method.equals("GET") && url.equals("/user/signup")) {
//                String query = url.split("\\?")[1];
//                Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(query);
//                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
//                repository.addUser(user);
//
//                response302Header(dos, HOME_URL);
//                return;
//            }

            // 요구사항 3: POST 방식으로 회원가입하기
            if (request.getMethod().equals(HttpMethod.POST.getMethod()) && request.getUrl().equals(SIGNUP_URI)) {
                String query = request.getBody();
                Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(query);
                User user = new User(queryParameter.get(UserQueryKey.ID.getQueryKey()), queryParameter.get(UserQueryKey.PASSWORD.getQueryKey()),
                        queryParameter.get(UserQueryKey.NAME.getQueryKey()), queryParameter.get(UserQueryKey.EMAIL.getQueryKey()));
                repository.addUser(user);

                // 요구사항 4: 302 status code 적용
                response.redirect(INDEX, false);
                return;
            }

            // 요구사항 5: 로그인하기
            if (request.getMethod().equals("POST") && request.getUrl().equals(LOGIN_URI)) {
                String query = request.getBody();
                Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(query);
                User user = repository.findUserById(queryParameter.get(UserQueryKey.ID.getQueryKey()));

                // 로그인 성공
                if ((user != null) && user.getPassword().equals(queryParameter.get(UserQueryKey.PASSWORD.getQueryKey()))) {
                    response.redirect(INDEX, true);
                    return;
                }

                // 로그인 실패
                response.redirect(LOGIN_FAILED_URL, false);
                return;
            }

            // 요구사항 6: 사용자 목록 출력
            if (request.getMethod().equals("GET") && request.getUrl().equals(LIST_URI)) {
                // 로그인이 되어있지 않은 상태
                if (!checkCookie(request)) {
                    response.redirect(LOGIN_URL, false);
                    return;
                }
                // 로그인이 되어있는 상태
                response.forward(LIST_URL);
            }

//            // 요구사항 7: CSS 출력
//            if (request.getUrl().endsWith("css")) {
//                type = "css";
//                body = Files.readAllBytes(Paths.get(ROOT + request.getUrl()));
//            }


        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private boolean checkCookie(HttpRequest request) {
        if (!request.hasHeaderKey(HttpHeader.COOKIE)) {
            System.out.println("no");
            return false;
        }
        if (!request.getHeaderValue(HttpHeader.COOKIE).contains("logined=true")) {
            return false;
        }
        return true;
    }
}
