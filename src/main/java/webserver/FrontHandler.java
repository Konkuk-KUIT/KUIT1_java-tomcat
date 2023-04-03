package webserver;

import webserver.CustomHandler.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class FrontHandler {

    private static final Map<String, CustomHandler> handlerMappingMap = new HashMap<>();
    private static FrontHandler frontHandler;

    private FrontHandler() {
    }

    public static FrontHandler getInstance() {
        if (handlerMappingMap.isEmpty()) {
            frontHandler = new FrontHandler();
            handlerMapping();
            return frontHandler;
        }
        return frontHandler;
    }

    private static void handlerMapping() {
        handlerMappingMap.put("/index.html", new IndexHandler());
        handlerMappingMap.put("/", new IndexHandler());
        handlerMappingMap.put("/user/form.html", new SignUpFormHandler());
        handlerMappingMap.put("/user/signup", new SignUpHandler());
        handlerMappingMap.put("/user/login.html", new LoginFormHandler());
        handlerMappingMap.put("/user/login_failed.html", new LoginFailFormHandler());
        handlerMappingMap.put("/user/login", new LoginHandler());
        handlerMappingMap.put("/user/userList", new UserListHandler());
    }

    public void service(HttpRequest request, HttpResponse response) throws IOException {

        CustomHandler handler = handlerMappingMap.get(request.getRequestUri());
        Map<String, String> paramMap = request.getParamMap();

        byte[] bytes = handler.process(paramMap);
        if (request.getHttpMethod().equals("POST")) {
            String byteToString = new String(bytes);
            if (request.getRequestUri().equals("/user/login")) {
                if (byteToString.equals("/index.html")) {
                    response302HeaderAndCookie(dos, byteToString);
                    return;
                }
            }
            response302Header(dos, byteToString);
            return;
        }
        response200Header(dos, bytes.length);
        responseBody(dos, bytes);

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

    private void response302Header(DataOutputStream dos, String viewName) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Location: " + viewName + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderAndCookie(DataOutputStream dos, String viewName) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Location: " + viewName + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
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
