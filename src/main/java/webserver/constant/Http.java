package webserver.constant;

public enum Http {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    LOGINED_TRUE("logined=true"),
    TEXT_CSS("text/css;charset=utf-8"),
    TEXT_HTML("text/html;charset=utf-8"),

    // url
    WEBAPP("webapp"),
    INDEX("/index.html"),
    USER_LOGIN_FAILED("/user/login_failed.html"),
    SIGNUP_FORM("/user/form.html"),
    LIST("/user/list.html"),
    LOGIN_FORM("/user/login.html"),

    //uri
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    CSS("/css"),
    USER_LIST("/user/userList"),

    // status code
    FOUND("302"),
    OK("200");


    private final String value;

    Http(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
