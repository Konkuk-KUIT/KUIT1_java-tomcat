package responsefactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ResponseHeader {
    /**
     *  상태코드에 따라 응답메세지의 헤더를 반환해주는 함수를 분리
     */
    private static final Logger log = Logger.getLogger(ResponseHeader.class.getName());
    public void response200Header(DataOutputStream dos, int lengthOfBodyContent) {

        /*** 단순 조회에 대한 성공 응답 코드 200 ***/

        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void responseCSS200Header(DataOutputStream dos, int lengthOfBodyContent) {

        /*** 단순 조회에 대한 성공 응답 코드 200 ***/

        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response302Header(DataOutputStream dos, String redirectURI) {

        /*** 페이지 리다이렉팅에 대한 응답 코드 302 ***/

        try{
            dos.writeBytes("HTTP/1.1 302 FOUND\r\n");
            dos.writeBytes("Location: "+redirectURI+"\r\n");
            dos.writeBytes("Content-Length: 0\r\n");
            dos.writeBytes("\r\n");
        }catch (IOException e){
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    public void response302LoginSuccessHeader(DataOutputStream dos, String cookieValue, String redirectURI){

        /*** 로그인 성공 후 쿠키 추가 및 페이지 리다이렉팅에 대한 응답 코드 302 ***/

        try{
            dos.writeBytes("HTTP/1.1 302 FOUND\r\n");
            dos.writeBytes("Location: "+redirectURI+"\r\n");
            dos.writeBytes("Set-Cookie: "+cookieValue+"\r\n");
            dos.writeBytes("Content-Length: 0\r\n");
            dos.writeBytes("\r\n");
        }catch (IOException e){
            log.log(Level.SEVERE,e.getMessage());
        }
    }
}
