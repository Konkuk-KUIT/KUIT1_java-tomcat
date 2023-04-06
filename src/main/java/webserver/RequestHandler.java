package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import model.User;
import responsefactory.ResponseBody;
import responsefactory.ResponseHeader;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final Repository userRepository =MemoryUserRepository.getInstance(); // User 관련 DB
    private static final ResponseBody responseBody = new ResponseBody(); //응답 메세지 바디 부분 반환 클래스
    private static final ResponseHeader responseHeader = new ResponseHeader(); //응답 메시지 헤더부분 반환 클래스
    private static final HttpRequestUtils httpRequestUtils = new HttpRequestUtils(); //요청메세지 해석 관련 도구
    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        /*** 요청이 들어옴 ***/
        try(BufferedReader is = new BufferedReader(new InputStreamReader(connection.getInputStream())); OutputStream os = connection.getOutputStream()){
            /*** request 변수에 요청 메시지 저장***/
            String request = is.readLine();
            log.log(Level.OFF, request);

            /**
             * requestParts 배열에 요청 메시지를 공백으로 분리해서 관리
             * requestParts[0] : HTTP 요청 메서드
             * requestParts[1] : 요청 경로
             * requestParts[2] : HTTP 프로토콜 버전
             * **/
            String[] requestParts=request.split(" ");

            if(requestParts.length!=3){
                throw new IllegalArgumentException("유효하지 않는 요청입니다.");
            }

            String method = requestParts[0];
            String path = requestParts[1];
            String version = requestParts[2];

            /*** 응답 메시지 만들기 시작! ***/

            byte[] body; // 응답 메세지 바디 부분 변수
            DataOutputStream dos = new DataOutputStream(os); // 출력 스트림 변수 (여기에 담아서 응답메세지 전달)
            if(path.equalsIgnoreCase("/")){

                /**
                 *  홈화면 요청 처리
                 *  localhost:80으로 들어온 경우
                 *  body에 index.html을 담고 헤더에 200 상태 코드 넣어서 응답메세지 반환
                 **/

                body = Files.readAllBytes(Paths.get("C:/Users/rkddu/KUIT Server/java-tomcat/webapp/index.html"));
                responseHeader.response200Header(dos, body.length);
                responseBody.responseBody(dos, body);

            }else if(path.startsWith("/user/signup")){

                /**
                 *  회원 가입 요청 처리
                 *  localhost/user/signup 으로 들어온 경우
                 *  queryString을 key, value로 분리하여 User 객체 생성 후 사용자 DB에 저장
                 *  헤더에 302 상태 코드 넣어서 응답메세지 반환 (홈화면으로 리다이렉트)
                 **/

                String queryString = path.length()>1 ? path.substring(path.indexOf('?')+1) : "";
                log.log(Level.INFO,queryString);
                Map<String,String> paramMap = httpRequestUtils.parseQueryString(queryString);

                User user=new User(paramMap.get("userId"),paramMap.get("password"),paramMap.get("name"),paramMap.get("email"));
                userRepository.addUser(user);

                //페이지 리다이렉팅!
                String redirectUrl = "http://localhost:80";
                responseHeader.response302Header(dos,redirectUrl);
                body=null;
                responseBody.responseBody(dos, body);
            }
            else {
                /**
                 *  그 외 페이지 요청 처리
                 *  localhost/요청 html 파일 경로로 들어온 경우
                 *  body에 해당 html 파일을 넣고 200 상태 코드 넣어서 응답메세지 반환
                 **/
                Path filePath = Paths.get("C:/Users/rkddu/KUIT Server/java-tomcat/webapp" + path);
                body = Files.readAllBytes(filePath);
                responseHeader.response200Header(dos, body.length);
                responseBody.responseBody(dos, body);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }



}
