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
    Repository memoryUserRepository = MemoryUserRepository.getInstance();
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String rawRequestInfo = br.readLine();
            String RequestInfo = rawRequestInfo.split(" HTTP/1.1")[0];
            String[] Info = RequestInfo.split(" ");
            String method = Info[0];
            String url = Info[1];
            String queryString = "";
            final String loginCookie = "logined=true;";
            final String loginFailedCookie = "logined=false;";
            int requestContentLength = 0;
            String requestCookie = "";

            byte body[];

            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith("Cookie")) {
                    requestCookie = line.split(":")[1];
                }
            }

            //기능 1
            if(url.equals("/")) url="/index.html";

            //queryString 분리 -get
            if(url.contains("?")){
                System.out.println(url);
                String[] strings = url.split("\\?");
                url = strings[0];
                queryString = strings[1];
            }

            //기능 2,3,4
            if(url.contains("user/signup")) {
                queryString = IOUtils.readData(br,requestContentLength);
                Map<String, String> user = HttpRequestUtils.parseQueryParameter(queryString);
                memoryUserRepository.addUser(new User(user.get("userId"),user.get("password"),user.get("name"),user.get("email")));
                response302Header(dos,"/index.html");
                return;
            }

            //기능 5
            if(url.equals("/user/login")){
                System.out.println("login in");
                queryString = IOUtils.readData(br,requestContentLength);
                Map<String, String> user = HttpRequestUtils.parseQueryParameter(queryString);
                User tempUser = memoryUserRepository.findUserById(user.get("userId"));
                if(tempUser!=null && user.get("password").equals(tempUser.getPassword())){
                    System.out.println(loginCookie);
                    response302HeaderWithCookie(dos,"/index.html",loginCookie);
                    return;
                }
                response302HeaderWithCookie(dos,"/user/login_failed.html",loginFailedCookie);
                return;
            }

            if(url.equals("/user/userList")){
                if(!requestCookie.isEmpty()&&requestCookie.split("=")[1].equals("true")){
                    System.out.println("목록");
                    response302Header(dos,"/user/list.html");
                    return;
                }
                response302Header(dos,"/user/login.html");
                return;
            }

            if(url.contains("css")){
                body = Files.readAllBytes(Paths.get("./webapp"+url));
                response200cssHeader(dos,body.length);
                responseBody(dos,body);
                return;
            }


            body = Files.readAllBytes(Paths.get("./webapp"+url));
            response200Header(dos,body.length);
            responseBody(dos,body);

            } catch (IOException ex) {
            throw new RuntimeException(ex);

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

    private void response200cssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String location, String content) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: "+ content + "\r\n");
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
