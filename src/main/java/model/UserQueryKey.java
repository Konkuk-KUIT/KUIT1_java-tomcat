package model;

public enum UserQueryKey {
    ID("userId"), PASSWORD("password"), NAME("name"), EMAIL("email");

    private String queryKey;

    UserQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }

    public String getQueryKey() {
        return queryKey;
    }
}
