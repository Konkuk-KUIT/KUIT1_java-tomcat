package http.constant;

import java.util.Arrays;

public enum RequestUrl {
    ROOT("./webapp"), INDEX("/index.html"),
    SIGNUP_URI("/user/signup"), FORM("/user/form.html"),
    LOGIN_URI("/user/login"), LOGIN_URL("/user/login.html"), LOGIN_FAILED_URL("/user/login_failed.html"),
    LIST_URI("/user/userList"), LIST_URL("/user/list.html");

    private String url;

    RequestUrl(String url) {
        this.url = url;
    }

    public static RequestUrl getRequestUrl(String path) {
        RequestUrl u =  Arrays.stream(RequestUrl.values())
                .filter(requestUrl -> requestUrl.url.equals(path))
                .findAny()
                .orElse(null);
        validateUrl(u);
        return u;
    }

    public String getUrl() {
        return url;
    }

    private static void validateUrl(RequestUrl requestUrl) {
        if (requestUrl == null) {
            throw new IllegalArgumentException("지원되지 않는 url입니다.");
        }
    }
}
