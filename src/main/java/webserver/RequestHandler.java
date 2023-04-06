package webserver;

import db.MemoryUserRepository;
import db.Repository;

import model.User;
import webserver.http.HttpMethod;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


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

            HttpRequest request = new HttpRequest(in);
            String path = getDefaultPath(request.getPath());
            HttpMethod method = request.getMethod();
            if (path.equals("/user/signup")) {
                User user = new User(
                        request.getParameter("userId"),
                        request.getParameter("password"),
                        request.getParameter("name"),
                        request.getParameter("email")
                );
                repository.addUser(user);
                response302Resource(out, HOME_URL);
            }
            if (path.equals("/user/login")) {
                String userId = request.getParameter("userId");
                User user = repository.findUserById(userId);
                if (user != null && user.getPassword().equals(request.getParameter("password"))) {
                    response302ResourceWithCookie(out, HOME_URL);
                }
                response302Resource(out, LOGIN_FAILED_URL);
            } else if (path.endsWith(".css")) {
                responseCssResource(out, path);
            } else {
                response200Resource(out, path);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseCssResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File(ROOT_URL + url).toPath());
        response200CssHeader(dos, body.length);
        responseBody(dos, body);
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return HOME_URL;
        }
        return path;
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

    private void response200Resource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File(ROOT_URL + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void response302Resource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File(ROOT_URL + url).toPath());
        response302Header(dos, url);
        responseBody(dos, body);
    }
    private void response302ResourceWithCookie(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File(ROOT_URL + url).toPath());
        response302HeaderWithCookie(dos, url);
        responseBody(dos, body);
    }
}