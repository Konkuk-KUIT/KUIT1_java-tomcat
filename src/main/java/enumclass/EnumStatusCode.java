package enumclass;

public enum EnumStatusCode {
    response302("302"), response200("200");

    private final String code;

    EnumStatusCode(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
