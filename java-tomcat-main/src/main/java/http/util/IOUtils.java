package http.util;

import java.io.BufferedReader;
import java.io.IOException;

public class IOUtils {
    /**
     *
     * @param br
     * socket으로부터 가져온 InputStream
     *
     * @param contentLength
     * 헤더의 Content-Length의 값이 들어와야한다.
     *
     */

    //bufferReader에서 contentLength만큼 정보가 들어온 것을 string형식으로 바꾸어 반환해줌.
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }
}
