package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.constant.HttpHeader;
import http.request.HttpRequest;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import http.constant.RequestUrl;
import model.User;

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

//            String a = br.readLine();
//            String[] lines = a.split(" ");
////            String[] lines = br.readLine().split(" ");
//            String method = lines[0];
//            String url = lines[1];
//
//            System.out.println(a);


            int requestContentLength = 0;
            boolean cookie = false;

            String type = "html";

//            while (true) {
//                final String line = br.readLine();
//                if (line.equals("")) {
//                    break;
//                }
//                // header info
//                if (line.startsWith("Content-Length")) {
//                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
//                }
//                if (line.startsWith("Cookie")) {
//                    if (line.split(": ")[1].equals("logined=true")) {
//                        cookie = true;
//                    }
//                }
//            }

            byte[] body = new byte[0];

            // 요구사항 1: index.html 반환하기
            if (request.getUrl().equals("/")) {
                body = Files.readAllBytes(Paths.get(ROOT + INDEX));
            }

            if (request.getMethod().equals("GET") && request.getUrl().endsWith(".html")) {
                body = Files.readAllBytes(Paths.get(ROOT + request.getUrl()));
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
            if (request.getMethod().equals("POST") && request.getUrl().equals(SIGNUP_URI)) {
                String query = request.getBody();
                Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(query);
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
                repository.addUser(user);

                // 요구사항 4: 302 status code 적용
                response302Header(dos, INDEX);
                return;
            }

            // 요구사항 5: 로그인하기
            if (request.getMethod().equals("POST") && request.getUrl().equals(LOGIN_URI)) {
                String query = request.getBody();
                Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(query);
                User user = repository.findUserById(queryParameter.get("userId"));

                // 로그인 성공
                if ((user != null) && user.getPassword().equals(queryParameter.get("password"))) {
                    response302HeaderWithCookie(dos, INDEX);
                    return;
                }

                // 로그인 실패
                response302Header(dos, LOGIN_FAILED_URL);
                return;
            }

            // 요구사항 6: 사용자 목록 출력
            if (request.getMethod().equals("GET") && request.getUrl().equals(LIST_URI)) {
                // 로그인이 되어있지 않은 상태
                if (!checkCookie(request)) {
                    response302Header(dos, LOGIN_URL);
                    return;
                }
                // 로그인이 되어있는 상태
                body = Files.readAllBytes(Paths.get(ROOT + LIST_URL));
            }

            // 요구사항 7: CSS 출력
            if (request.getUrl().endsWith("css")) {
                type = "css";
                body = Files.readAllBytes(Paths.get(ROOT + request.getUrl()));
            }

            response200Header(dos, body.length, type);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private boolean checkCookie(HttpRequest request) {
        if (!request.hasHeaderKey(HttpHeader.SET_COOKIE)) {
            return false;
        }
        if (!request.getHeaderValue(HttpHeader.SET_COOKIE).contains("logined=true")) {
            return false;
        }
        return true;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String type) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/" + type + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
