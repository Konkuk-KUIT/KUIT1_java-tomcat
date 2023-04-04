package http.util.constant;

public enum URL {
    ROOT("./webapp"), MAIN_PATH("/index.html"), LOGIN_PATH("/user/login.html"), LOGIN_FAILED_PATH("/user/login_failed.html"), UESRLIST_PATH("/user/list.html");

    private String url;

    URL(String url){
        this.url = url;
    }

    public String getURL() {
        return url;
    }

}

