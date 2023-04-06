package http.constant;

public enum HttpStatus {
    OK("200 OK"), REDIRECT("302 FOUND");

    private final String status;

    HttpStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
