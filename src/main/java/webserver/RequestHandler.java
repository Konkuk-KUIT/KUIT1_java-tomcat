package webserver;

import http.constants.HttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.session.HttpSessions;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            if (httpRequest.getCookie(HttpSessions.SESSION_ID_NAME) == null) {
                setSessionId(httpRequest, httpResponse);
            }

            RequestMapper requestMapper = new RequestMapper(httpRequest,httpResponse);
            requestMapper.proceed();

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private static void setSessionId(HttpRequest httpRequest, HttpResponse httpResponse) {
        String jsessionId = UUID.randomUUID().toString();
        httpResponse.put(HttpHeader.SET_COOKIE,"JSESSIONID=" + jsessionId);
        httpRequest.addCookie(HttpSessions.SESSION_ID_NAME,jsessionId);
    }
}
