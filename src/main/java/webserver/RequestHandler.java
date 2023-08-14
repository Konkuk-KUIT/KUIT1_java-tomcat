package webserver;

import java.io.*;
import java.net.Socket;

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
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            RequestMapping requestMapper = new RequestMapping(request, response);
            requestMapper.proceed();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}