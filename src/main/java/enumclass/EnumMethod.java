package enumclass;

public enum EnumMethod {
    POST("POST"), GET("GET");

    private final String method;

    EnumMethod(String method){
        this.method = method;
    }

    public String getMethod(){
        return method;
    }

}
