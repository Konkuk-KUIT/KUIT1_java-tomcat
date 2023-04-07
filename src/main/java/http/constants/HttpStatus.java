package http.constants;


//HTTP 상태 코드 enum 클래스
public enum HttpStatus {
    //200 OK, 302 Redirect
    OK("OK"), REDIRECT("Found");

    private final String status;

    HttpStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
