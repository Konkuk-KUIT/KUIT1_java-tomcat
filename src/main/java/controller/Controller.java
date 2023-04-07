package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;

import java.io.IOException;

public interface Controller {
    static final String ROOT_URL = "./webapp";
    static final String HOME_URL = "/index.html";
    static final String LOGIN_FAILED_URL = "/user/login_failed.html";
    static final String LIST_URL = "/user/list.html";
    static final String LOGIN_URL = "/user/login.html";
    static final String STYLE_CSS_URL = "/css/styles.css";
    static final String HTML_TYPE = "text/html";
    static final String CSS_TYPE = "text/css";
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
