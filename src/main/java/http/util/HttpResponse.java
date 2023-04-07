package http.util;

import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enumclass.EnumHeader.*;

public class HttpResponse {
    DataOutputStream dos;
    byte[] body = new byte[0];
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public HttpResponse(OutputStream os) {
        this.dos = new DataOutputStream(os);
    }

    public void forward(String path, String type) throws IOException {
        body = Files.readAllBytes(Paths.get(path));
        response200Header(body.length, type);
        responseBody(body);
    }

    public void redirect(String path, Boolean cookie) throws IOException{
        response302Header(path, cookie);
    }

    private void response302Header(String path, Boolean cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes(location.getHeader() + ": " + path + "\r\n");
            if(cookie){
                log.warning("true");
                dos.writeBytes(setCookie.getHeader() + ": logined=true \r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(int lengthOfBodyContent, String type) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(contentType.getHeader() + ": "+ type + ";charset=utf-8\r\n");
            dos.writeBytes(contentLength.getHeader() + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
