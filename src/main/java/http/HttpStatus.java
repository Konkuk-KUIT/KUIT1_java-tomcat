package http;

public enum HttpStatus {
    OK(200), REDIRECT(302);

    private int status;

    HttpStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
