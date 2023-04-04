package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static java.lang.System.console;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String ROOT_URL = "./webapp";
    private static final String HOME_URL = "/index.html";
    private static final String LOGIN_FAILED_URL = "/user/login_failed.html";
    private static final String LIST_URL = "/user/list.html";
    private static final String LOGIN_URL = "/user/login.html";
    private static final String STYLE_CSS_URL = "/css/style.css";
    private final Repository repository;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        // try-with-resource -> connection의 inputStream과 outputStream 자원 반납
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = new byte[0];

            // Header 분석
            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];

            int requestContentLength = 0;
            String cookie = "";

            while (true){
                final String line = br.readLine();

                if(line.equals("")){
                    break;
                }

                // Header info
                if(line.startsWith("Content-Length")){
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }

                if(line.startsWith("Cookie")){
                    cookie = line.split(": ")[1];
                }
            }

            // 요구사항 1 : index.html 반환하기
            if(method.equals("GET") && url.equals("index.html")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }

            if(method.equals("GET") && url.equals("/")){
                body = Files.readAllBytes(Paths.get(ROOT_URL+HOME_URL));
            }

            // 요구사항 2 : GET 방식으로 회원가입하기
            if(method.equals("GET") && url.contains("/user/signup")){
                Map<String, String> queryParameter = parseQueryParameter(url.split("\\?")[1]);
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
                repository.addUser(user);
                response302Header(dos,HOME_URL);
                return;
            }

            // 요구사항 3 : POST 방식으로 회원가입하기
            if (method.equals("POST") && url.equals("/user/signup")) {
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
                repository.addUser(user);
                response302Header(dos,HOME_URL);
                return;
            }

            // 요구사항 5 : 로그인하기
            if (url.equals("/user/login")){
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                String userid = queryParameter.get("userId");
                User user = repository.findUserById(userid);
                login(dos, queryParameter, user);
                return;
            }

            // 요구사항 6 : 사용자 목록 출력
            if (url.equals("/user/userList")){
                if(!cookie.equals("logined=true")){
                    response302Header(dos,LOGIN_URL);
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT_URL+LIST_URL));
            }

            if(url.endsWith(".css")){
                Path path = Paths.get(ROOT_URL+STYLE_CSS_URL);
                body = Files.readAllBytes(path);
                response200HeaderCSS(dos, body.length);
                return;
            }


            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }



    private  void login(DataOutputStream dos, Map<String, String> queryParameter, User user){
        if(user != null && queryParameter.get("password").equals(user.getPassword())){
            response302HeaderWithCookie(dos,HOME_URL);
            return;
        }
        response302Header(dos, LOGIN_FAILED_URL);
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

    private void response302HeaderWithCookie(DataOutputStream dos, String path){
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
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

    private void response200HeaderCSS(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
