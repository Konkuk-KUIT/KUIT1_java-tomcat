package model.constants;

public enum UserQueryKey {
    ID("userId"), PASSWORD("password"), NAME("name"), EMAIL("email");

    private final String key;

    UserQueryKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
