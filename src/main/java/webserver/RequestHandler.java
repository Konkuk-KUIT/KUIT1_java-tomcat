package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final String ROOT = "./webapp";
    private static final String MAIN_URL = "/index.html";
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

            byte[] body = new byte[0];
            body = readRequest(br, dos);

            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private byte[] readRequest(BufferedReader br, DataOutputStream dos) throws IOException {

        String startLine = br.readLine();
        String[] startLines = startLine.split(" ");
        String method = startLines[0];
        String requestUrl = startLines[1];
        String HTTP_version = startLines[2];

        log.log(Level.INFO, "<" + requestUrl + ">");
        byte[] body = new byte[0];


        /* ****************************
                    요구사항 1
         ****************************** */
        // 아무런 입력이 없다면 MAIN_URL(==index.html)로
        if(requestUrl.equals("/")){
            requestUrl = MAIN_URL;
        }
        if(method.equals("GET") && requestUrl.contains(".html")) {
            body = Files.readAllBytes(Paths.get(ROOT + requestUrl));
        }

        return body;
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
