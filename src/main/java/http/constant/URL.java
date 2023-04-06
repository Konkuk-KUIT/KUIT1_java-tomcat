package http.constant;

import java.util.Arrays;

public enum URL {
    USERLIST_PATH("/user/list.html"), USERLIST_ACTION("/user/userList"), LOGIN_ACTION("/user/login"), SIGNUP_ACTION("/user/signup"), ROOT("./webapp"), MAIN_PATH("/index.html"), LOGIN_PATH("/user/login.html"), LOGIN_FAILED_PATH("/user/login_failed.html");

    private String url;

    URL(String url){
        this.url = url;
    }

    public static URL of(String url) {
        return Arrays.stream(values())
                .filter(u -> url.equals(u.url))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("처리가 불가능한 url입니다.")));
    }

    public String getURL() {
        return url;
    }

}

