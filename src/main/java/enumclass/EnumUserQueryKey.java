package enumclass;

public enum EnumUserQueryKey {
    USERID("userId"), PASSWORD("password"), NAME("name"), EMAIL("email");

    private final String key;

    EnumUserQueryKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
