package enumclass;

public enum EnumHeader {
    contentLength("Content-Length"), contentType("Content-Type"), cookie("Cookie"), location("Location"), setCookie("Set-Cookie");

    private final String header;

    EnumHeader(String header){
        this.header = header;
    }

    public String getHeader(){
        return header;
    }
}
