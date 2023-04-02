package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
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
    private static final String ROOT = "./webapp";
    private static final String MAIN_PATH = "/index.html";

    private final Repository repository;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());


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

            readRequest(br, dos);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void readRequest(BufferedReader br, DataOutputStream dos) throws IOException {

        String startLine = br.readLine();
        String[] startLines = startLine.split(" ");
        String method = startLines[0];
        String requestUrl = startLines[1];
        String HTTP_version = startLines[2];

        byte[] body = new byte[0];

        int contentLength = 0;
        String cookie = "";

        // header 정보 파싱
        while (true) {
            final String line = br.readLine();

            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
            if (line.startsWith("Cookie")) {
                cookie = line.split(": ")[1].strip();
            }
        }

        /* ****************************
                    요구사항 1
         ****************************** */
        // 아무런 입력이 없다면 MAIN_URL(==index.html)로
        if(requestUrl.equals("/")){
            requestUrl = MAIN_PATH;
        }
        if(method.equals("GET") && requestUrl.endsWith(".html")) {
            body = Files.readAllBytes(Paths.get(ROOT + requestUrl));
        }


        /* ****************************
                 요구사항 2, 3, 4
         ****************************** */
        if(requestUrl.equals("/user/signup")) {
            String query = null;
            if(method.equals("GET"))
                query = requestUrl.split("\\?")[1];
            if(method.equals("POST"))
                query = IOUtils.readData(br, contentLength);
            Map<String, String> queries = HttpRequestUtils.parseQueryParameter(query);
            User user = new User(queries.get("userId"), queries.get("password"), queries.get("name"), queries.get("email"));
            repository.addUser(user);
            response302Header(dos, MAIN_PATH);
            return;
        }


        /* ****************************
                    요구사항 5
         ****************************** */
        if(requestUrl.equals("/user/login")){ // login.html에 action 존재. post 방식
            String query = IOUtils.readData(br, contentLength);
            Map<String, String> queries = HttpRequestUtils.parseQueryParameter(query);
            User user = repository.findUserById(queries.get("userId"));
            if(user != null && user.getPassword().equals(queries.get("password"))){
                response302HeaderWithCookie(dos, MAIN_PATH);
                return;
            }
            // 실패시
            response302Header(dos, "/user/login_failed.html");
        }

        /* ****************************
                    요구사항 6
         ****************************** */
        if(requestUrl.equals("/user/userList")){ // login.html에 action 존재. post 방식
            if(!cookie.equals("logined=true")){
                // 로그인 하지 않은 유저
                response302Header(dos, "/user/login.html");
                return;
            }
            response302Header(dos,  "/user/list.html");
            return;
        }

        /* ****************************
                    요구사항 7
         ****************************** */
        if(requestUrl.endsWith(".css")){
            body = Files.readAllBytes(Paths.get(ROOT + requestUrl));
            response200HeaderCss(dos, body.length);
            responseBody(dos, body);
            return;
        }

        response200Header(dos, body.length);
        responseBody(dos, body);

    }

    private void response200HeaderCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }

    }

    private void response302HeaderWithCookie(DataOutputStream dos, String redirectURL) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectURL + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true" + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectURL) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectURL+"\r\n");
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
