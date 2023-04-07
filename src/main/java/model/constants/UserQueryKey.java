package model.constants;

//User 정보 Enum 클래스
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
