package http.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static http.util.constant.Status.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstantTest {

    @Test
    @DisplayName("Status - 문자열 생성 테스트")
    public void testStatus(){
        String s = "HTTP/1.1 " + OK.getStatus();
        assertEquals(s,"HTTP/1.1 200 OK");
    }
    
}
