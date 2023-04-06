package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;


public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String ROOT_URL = "./webapp";
    private static final String HOME_URL = "/index.html";
    private static final String LOGIN_FAILED_URL = "/user/login_failed.html";
    private static final String LOGIN_URL = "/user/login.html";
    private static final String LIST_URL = "/user/list.html";
    private final Repository repository;
    private final Path defaultPath = Paths.get(ROOT_URL + HOME_URL);

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String requestLine = br.readLine(); // 첫 줄을 읽고
            String[] tokens = requestLine.split(" "); // 공백단위로 분리
            String method = tokens[0]; // get, post, ...
            String url = tokens[1]; // url

            byte[] body = new byte[0];
            int contentLength = 0;
            String cookie = "";

            while (!requestLine.equals("")) {
                System.out.println(requestLine);
                requestLine = br.readLine();
                if (requestLine.contains("Content-Length")) {
                    contentLength = getContentLength(requestLine);
                }
                if (requestLine.startsWith("Cookie")) {
                    cookie = requestLine.split(": ")[1];
                }
            }

            if (method.equals("GET") && url.endsWith(".html")) {
                // .html 을 요청하는 경우 해당 경로를 Byte 로 만들어 준 다음 outputStream 으로
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }
            if (url.equals("/")) { // default
                body = Files.readAllBytes(defaultPath);
            }
            if (url.equals("/user/signup")) { // 회원가입
                String httpBody = IOUtils.readData(br, contentLength);
                Map<String, String> parasMap = parseQueryParameter(httpBody);

                repository.addUser(new User(
                        parasMap.get("userId"),
                        parasMap.get("password"),
                        parasMap.get("name"),
                        parasMap.get("email")
                ));
                response302Header(dos, HOME_URL);
                return;
            }
            if (url.equals("/user/login")) { // 로그인 페이지
                String httpBody = IOUtils.readData(br, contentLength);
                Map<String, String> parasMap = parseQueryParameter(httpBody);
                User user = repository.findUserById(parasMap.get("userId"));
                logIn(dos, parasMap, user);
                return;
            }
            if (url.equals("/user/userList")) {
                if (!cookie.equals("logined=true")) { // 로그인한 상태가 아니라면
                    response302Header(dos, LOGIN_URL); // 로그인 페이지로
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT_URL + LIST_URL));
            }
            if (url.endsWith(".css")) {
                response200CssHeader(dos, body.length);
                responseBody(dos, body);
                return;
            }
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void logIn(DataOutputStream dos, Map<String, String> queryParams, User user) {
        if (user != null && user.getPassword().equals(queryParams.get("password"))) {
            response302HeaderWithCookie(dos, HOME_URL);
        }
        response302Header(dos, LOGIN_FAILED_URL);
    }

    private int getContentLength(String requestLine) {
        return Integer.parseInt(requestLine.split(": ")[1]);
    }


    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        // HTTP 200 OK 는 요청이 성공했음을 나타내는 성공 응답 상태 코드입니다. 200 응답은 기본적으로 캐시 가능합니다.
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        // HTTP 200 OK 는 요청이 성공했음을 나타내는 성공 응답 상태 코드입니다. 200 응답은 기본적으로 캐시 가능합니다.
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        //  302 Found 리디렉션 상태 응답 코드는 요청한 리소스가 Location (en-US) 헤더에 지정된 URL 으로 일시적으로 이동되었음을 나타냅니다.
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true" + "\r\n");
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
