package webserver;

import http.util.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static webserver.constant.Http.*;

public class HttpResponse {

    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private DataOutputStream dos;

    private String statusCode;
    private final Map<String, String> headerMap = new HashMap<>();

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
        try {
            createStartLine();
            createHeader();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void forward(String path) {
        try {
            File file = new File(path);
            BufferedReader fr = new BufferedReader(new FileReader(file));
            String fileData = IOUtils.readData(fr, (int) file.length());
            responseBody(fileData.getBytes());
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        setHeader(CONTENT_TYPE.getValue(), TEXT_CSS.getValue());
    }

    private void createStartLine() throws IOException {
        String result = statusCode == null ? OK.getValue() : statusCode;
        dos.writeBytes("HTTP/1.1 " + result + " OK \r\n");
    }

    private void createHeader() throws IOException {
        dos.writeBytes(CONTENT_TYPE.getValue() + ": " + TEXT_HTML.getValue() + "\r\n");
        for (String s : headerMap.keySet()) {
            dos.writeBytes(s + ": " + headerMap.get(s) + " \r\n");
        }
    }

    private void responseBody(byte[] body) throws IOException {
        dos.writeBytes(CONTENT_LENGTH.getValue() + body.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }

    public void setHeader(String fieldName, String fieldValue) {
        headerMap.put(fieldName, fieldValue);
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
