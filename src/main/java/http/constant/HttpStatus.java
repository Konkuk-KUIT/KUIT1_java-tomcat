package http.constant;

import java.util.Arrays;

public enum HttpStatus {
    OK(200), REDIRECT(302);

    private int status;

    HttpStatus(int status) {
        this.status = status;
    }

    public static HttpStatus getHttpStatus(int code) {
        HttpStatus c =  Arrays.stream(HttpStatus.values())
                .filter(httpStatus -> httpStatus.status == code)
                .findAny()
                .orElse(null);
        validateHttpStatus(c);
        return c;
    }

    public int getStatus() {
        return status;
    }

    private static void validateHttpStatus(HttpStatus code) {
        if (code == null) {
            throw new IllegalArgumentException("지원되지 않는 상태코드입니다.");
        }
    }
}
