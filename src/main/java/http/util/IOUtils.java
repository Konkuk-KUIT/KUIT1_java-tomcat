package http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;

public class IOUtils {
    /**
     * 요청 메세지의 body 부분을 읽는 함수
     *
     * @param br
     * socket으로부터 가져온 InputStream
     *
     * @param contentLength
     * 헤더의 Content-Length의 값이 들어와야한다.
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

    /**
     * 응답메세지에서 헤더 부분의 Content-Length 부분을 찾아서 반환하는 함수
     *
     * @param br
     * @return
     * @throws IOException
     *
     */

    public static Integer getContentLength(BufferedReader br) throws IOException {
        Integer contentLength = 0;
        while (true) {
            //content-length 추출 반복문
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }
        return contentLength;
    }

    public static Boolean checkCoockie(BufferedReader br) throws IOException {
        Boolean CookieExists=false;
        while (true) {
            //content-length 추출 반복문
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith("Cookie")) {
                if(line.split(": ")[1].equals("logined=true")){
                    CookieExists=true;
                }
            }
        }
        return CookieExists;
    }
}
