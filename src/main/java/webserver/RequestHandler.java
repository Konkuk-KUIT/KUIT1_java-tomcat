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
    private final Path homePath = Paths.get(ROOT_URL + HOME_URL);

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

            String requestLine = br.readLine(); //
            String[] tokens = requestLine.split(" "); // 분리
            String method = tokens[0];
            String url = tokens[1];

            byte[] body = new byte[0];

            int contentLength = 0;
            String cookie = "";

            while (!requestLine.equals("")) {
                requestLine = br.readLine();
                if (requestLine.contains("Content-Length")) {
                    contentLength = getContentLength(requestLine);
                }
                if (requestLine.startsWith("Cookie")) {
                    cookie = requestLine.split(": ")[1];
                }
            }

            if (method.equals("GET") && url.endsWith(".html")) { // 01. index.html 반환
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }
            if (url.equals("/")) {
                body = Files.readAllBytes(homePath);
            }
            if (url.equals("/user/signup")) {
                String httpBody = IOUtils.readData(br, contentLength);
                Map<String, String> parasMap = parseQueryParameter(httpBody);

                String userId = parasMap.get("userId");
                String password = parasMap.get("password");
                String name = parasMap.get("name");
                String email = parasMap.get("email");

                repository.addUser(new User(userId, password, name, email));

                response302Header(dos, HOME_URL);
                return;
            }

            if (url.equals("/user/login")) {
                String httpBody = IOUtils.readData(br, contentLength);
                Map<String, String> parasMap = parseQueryParameter(httpBody);
                User user = repository.findUserById(parasMap.get("userId"));
                logIn(dos, parasMap, user);
                return;
            }

            if (url.equals("/user/userList")) {
                if (!cookie.equals("logined=true")) {
                    response302Header(dos, LOGIN_URL);
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT_URL + LIST_URL));
            }

            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void logIn(DataOutputStream dos, Map<String, String> queryParams, User user) {
        if (user.getPassword().equals(queryParams.get("password"))) {
            response302HeaderWithCookie(dos, HOME_URL);
        }
        response302Header(dos, LOGIN_FAILED_URL);
    }

    private int getContentLength(String requestLine) {
        return Integer.parseInt(requestLine.split(": ")[1]);
    }


    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
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
