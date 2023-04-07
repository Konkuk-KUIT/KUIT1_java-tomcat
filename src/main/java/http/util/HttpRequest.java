package http.util;

import enumclass.EnumHeader;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import static enumclass.EnumHeader.contentLength;
import static enumclass.EnumHeader.cookie;

public class HttpRequest {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    String startLine;
    String header;
    String body;
    String cookie;
    public HttpRequest(String startLine, String header, String body, String cookie) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
        this.cookie = cookie;
    }
    public static HttpRequest from(BufferedReader br) throws IOException {
        String startLine = br.readLine();

        int requestContentLength = 0;
        String cookie = "";
        String header = "";

        while (true){
            final String line = br.readLine();

            if(line.equals("")){
                break;
            }

            // Header info
            if(line.startsWith(contentLength.getHeader())){
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }

            if(line.startsWith(EnumHeader.cookie.getHeader())){
                cookie = line.split(": ")[1];
            }
        }
        String body = IOUtils.readData(br, requestContentLength);

        return new HttpRequest(startLine, header, body, cookie);
    }
    public String getCookie(){
        return cookie;
    }
    public String getURL(){
        return startLine.split(" ")[1];
    }
    public String getMethod(){
        return startLine.split(" ")[0];
    }
    public String getBody(){
        return body;
    }
}
