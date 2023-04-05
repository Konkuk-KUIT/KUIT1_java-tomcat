package http.constant;

import java.util.Arrays;

public enum RequestUrl {
    ROOT("./webapp"), INDEX("/index.html"),
    SIGNUP_URI("/user/signup"),
    LOGIN_URI("/user/login"), LOGIN_URL("/user/login.html"), LOGIN_FAILED_URL("/user/login_failed.html"),
    LIST_URI("/user/userList"), LIST_URL("/user/list.html");

    private String url;

    RequestUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
