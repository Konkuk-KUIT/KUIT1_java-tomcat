package http.request;

public enum RequestURL {
    USER_LIST_HTML("/user/list.html"),USER_LIST("/user/userList"),LOGIN("/user/login.html"),LOGIN_POST("/user/login"),LOGIN_FAILED("/user/login_failed.html"),SIGNUP("/user/signup"), ROOT("./webapp"),INDEX("/index.html");

    private String url;

    RequestURL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
