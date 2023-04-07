package enumclass;

public enum EnumURL {
    SIGNUP("/user/signup"), LOGIN("/user/login"), USERLIST("/user/userList"), HTML(".html"), CSS(".css");

    private final String url;

    EnumURL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
