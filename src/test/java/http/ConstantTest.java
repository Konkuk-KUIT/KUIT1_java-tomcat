package http;

import http.constant.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static http.constant.Status.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ConstantTest {

    @Test
    @DisplayName("Status - 문자열 생성 테스트")
    public void testStatus(){
        String s = "HTTP/1.1 " + OK.getStatus();
        assertEquals(s,"HTTP/1.1 200 OK");

        String ok = "200 OK";

    }

    @Test
    @DisplayName("HttpMethod - of 테스트")
    public void testMethod(){
        String startLine_method = "GET";
        HttpMethod method = HttpMethod.of(startLine_method);

        assertEquals("GET", method.getMethod());
        assertNotEquals("POST", method.getMethod());

    }


}
