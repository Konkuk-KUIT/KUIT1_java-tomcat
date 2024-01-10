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
    private final DataOutputStream dos;

    private final HttpResponseStartLine httpResponseStartLine;
    private final Map<String, String> headerMap;
    private String body;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
        httpResponseStartLine = new HttpResponseStartLine();
        headerMap = new HashMap<>();
    }

    public void forward(String path) {
        isHtml(path);

        try {
            File file = new File(WEBAPP.getValue() + path);
            BufferedReader fr = new BufferedReader(new FileReader(file));
            body = IOUtils.readData(fr, (int) file.length());
            responseBody();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void redirect(String location) throws IOException {
        httpResponseStartLine.setStatusCode("302");
        httpResponseStartLine.setStatus(FOUND);
        addHeader(LOCATION.getValue(), location);
        responseBody();
    }

    public void addHeader(String fieldName, String fieldValue) {
        headerMap.put(fieldName, fieldValue);
    }


    private void responseBody() throws IOException {
        dos.writeBytes(httpResponseStartLine + " \r\n");

        for (String s : headerMap.keySet()) {
            dos.writeBytes(s + ": " + headerMap.get(s) + " \r\n");
        }

        byte[] bytes = body.getBytes();
        dos.writeBytes(CONTENT_LENGTH.getValue() + bytes.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(bytes, 0, bytes.length);
        dos.flush();
        dos.close();
    }

    private void isHtml(String path) {
        if (path.contains(".html"))
            addHeader(CONTENT_TYPE.getValue(), TEXT_HTML.getValue());
        else
            addHeader(CONTENT_TYPE.getValue(), TEXT_CSS.getValue());

    }
}
