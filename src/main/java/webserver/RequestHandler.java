package webserver;

import webserver.CustomHandler.CustomHandler;
import webserver.CustomHandler.IndexHandler;
import webserver.CustomHandler.UserFormHandler;

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
        handlerMappingMap.put("/user/form.html", new UserFormHandler());
    }

    @Override
    public void run() {
        log.log(Level.INFO, "[RequestHandler] New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        handlerMapping();

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            CustomHandler handler = handlerMappingMap.get(getRequestTarget(br));

            byte[] bytes = handler.process();

            response200Header(dos, bytes.length);
            responseBody(dos, bytes);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private static String getRequestTarget(BufferedReader br) throws IOException {
        String startLine = br.readLine();
        String[] split = startLine.split(" ");
        return split[1];
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
