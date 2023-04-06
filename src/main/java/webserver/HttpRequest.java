package webserver;

import java.io.InputStream;
import java.util.logging.Logger;

public class HttpRequest {
    private static final Logger logger  = Logger.getLogger(HttpRequest.class.getName());
    private String path;
    private String method;


    public HttpRequest(InputStream input) {

    }

    public String getMethod() {
    }

    public String getPath() {
    }

    public String getHeader(String connection) {
    }

    public String getParameter(String userId) {
    }
}
