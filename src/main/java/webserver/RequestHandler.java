package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final Repository userRepository =MemoryUserRepository.getInstance();
    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try(BufferedReader is = new BufferedReader(new InputStreamReader(connection.getInputStream())); OutputStream os = connection.getOutputStream()){
            String request = is.readLine();
            log.log(Level.OFF, request);
            String[] requestParts=request.split(" ");

            if(requestParts.length!=3){
                throw new IllegalArgumentException("유효하지 않는 요청입니다.");
            }

            String method = requestParts[0]; //HTTP 요청 메서드
            String path = requestParts[1]; //요청 경로
            String version = requestParts[2]; //HTTP 프로토콜 버전
            byte[] body;
            log.log(Level.INFO,path);
            DataOutputStream dos = new DataOutputStream(os);
            if(path.equalsIgnoreCase("/")){
                /******* 1단계 **********/
                log.log(Level.OFF,"localhost:80");
                body = Files.readAllBytes(Paths.get("C:/Users/rkddu/KUIT Server/java-tomcat/webapp/index.html"));
                response200Header(dos, body.length);
            }else if(path.startsWith("/user/signup")){
                /******* 2단계 **********/
                //회원 가입 요청 처리
                //path.indexOf('?')을 이용하여 ? 문자가 있는 인덱스를 찾아낸 다음,
                //substring 메소드를 사용하여 ? 문자 이후의 문자열을 추출한다.
                String queryString = path.length()>1 ? path.substring(path.indexOf('?')+1) : "";
                log.log(Level.INFO,queryString);
                Map<String,String> paramMap = parseQueryString(queryString);

                User user=new User(paramMap.get("userId"),paramMap.get("password"),paramMap.get("name"),paramMap.get("email"));
                userRepository.addUser(user);

                //페이지 리다이렉팅!
                String redirectUrl = "http://localhost:80";
                response302Header(dos,redirectUrl);
                body=null;
            }
            else {
                /******* 전체적으로 요청에 해당하는 파일 반환하는 부분 **********/
                Path filePath = Paths.get("C:/Users/rkddu/KUIT Server/java-tomcat/webapp" + path);
                body = Files.readAllBytes(filePath);
                response200Header(dos, body.length);
            }
            //byte[] body = "Hello World".getBytes();
            //byte[] body = Files.readAllBytes(Paths.get("C:/Users/rkddu/KUIT Server/java-tomcat/webapp/index.html"));
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private Map<String,String> parseQueryString(String queryString){
        Map<String,String> paramMap = new HashMap<>();
        if(!queryString.isEmpty()){
            String[] parts=queryString.split("&");
            for(String part:parts) {
                String[] keyValue = part.split("=");
                String key=keyValue[0]; //key는 아이디, 비번, 이름, 이메일 주소 "타입 정보!"
                log.log(Level.INFO,key);
                String value = keyValue.length > 1 ? keyValue[1] : ""; //value는 해당 타입에 대한 실제 데이터 정보!
                //{key: value, key: value, key: value, key: value} 형식!
                try{
                    value= URLDecoder.decode(value,"UTF-8");
                    log.log(Level.INFO,value);
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                paramMap.put(key,value);
            }
        }
        return paramMap;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectURI) {
        try{
            dos.writeBytes("HTTP/1.1 302 FOUND\r\n");
            dos.writeBytes("Location: "+redirectURI+"\r\n");
            dos.writeBytes("Content-Length: 0\r\n");
            dos.writeBytes("\r\n");
        }catch (IOException e){
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
