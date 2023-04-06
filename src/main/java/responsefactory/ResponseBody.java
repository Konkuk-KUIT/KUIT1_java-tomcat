package responsefactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResponseBody {
    /**
     *  응답메세지의 바디 부분을 반화해주는 클래스
     */
    private static final Logger log = Logger.getLogger(ResponseBody.class.getName());
    public void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
