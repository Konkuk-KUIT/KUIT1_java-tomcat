package model;

public enum UserQueryKey {
    USERID("userId"), PASSWORD("password"), NAME("name"), EMAIL("email");
    private String key;

    UserQueryKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
