package webserver;

import http.util.HttpRequestUtils;
import http.util.IOUtils;
import webserver.CustomHandler.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final Map<String, CustomHandler> handlerMappingMap = new HashMap<>();


    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    private void handlerMapping() {
        handlerMappingMap.put("/index.html", new IndexHandler());
        handlerMappingMap.put("/", new IndexHandler());
        handlerMappingMap.put("/user/form.html", new SignUpFormHandler());
        handlerMappingMap.put("/user/signup", new SignUpHandler());
        handlerMappingMap.put("/user/login.html", new LoginFormHandler());
        handlerMappingMap.put("/user/login_failed.html", new LoginFailFormHandler());
        handlerMappingMap.put("/user/login", new LoginHandler());
    }

    @Override
    public void run() {
        log.log(Level.INFO, "[RequestHandler] New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        handlerMapping();

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String startLine = getStartLine(br);
            String uri = getURIFromRequestTarget(getRequestTarget(startLine));
            CustomHandler handler = handlerMappingMap.get(uri);

            Map<String, String> paramMap =
                    HttpRequestUtils.parseQueryParameter(IOUtils.readData(br, getRequestContentLength(br)));

            byte[] bytes = handler.process(paramMap);
            if (getHTTPMethod(startLine).equals("POST")) {
                String byteToString = new String(bytes);
                if (uri.equals("/user/login")) {
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

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private static int getRequestContentLength(BufferedReader br) throws IOException {
        int requestContentLength = 0;
        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }
        return requestContentLength;
    }

    private static String getHTTPMethod(String startLine) throws IOException {
        String[] split = startLine.split(" ");
        return split[0];
    }

    private static String getStartLine(BufferedReader br) throws IOException {
        return br.readLine();
    }

    private static String getRequestTarget(String startLine) throws IOException {
        String[] split = startLine.split(" ");
        return split[1];
    }

    private static String getURIFromRequestTarget(String RequestTarget) throws IOException {
        String[] split = RequestTarget.split("\\?");
        return split[0];
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
