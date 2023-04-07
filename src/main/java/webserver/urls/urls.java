package webserver.urls;

public enum urls {
    USER_LIST_HTML("/user/list.html"),
    USER_LIST("/user/userList"),
    LOGIN("/user/login.html"),
    LOGIN_POST("/user/login"),
    LOGIN_FAILED("/user/login_failed.html"),
    SIGNUP("/user/signup"),
    ROOT("./webapp"),
    INDEX("/index.html");

    private final String url;

    urls(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}