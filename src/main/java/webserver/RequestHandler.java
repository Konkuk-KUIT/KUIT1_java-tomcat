package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    //요구사항.1

    //Path pathIndex = Paths.get("/Users/lyouxsun/Desktop/java-tomcat/webapp/index.html");
    //FileInputStream input = new FileInputStream("/Users/lyouxsun/Desktop/java-tomcat/webapp/index.html");

    private static final String ROOT_URL =  "./webapp";
    private static final String HOME_URL = "/index.html";
    //private static final String LOGIN_FAILED_URL = "/user/login_failed.html";
    //private static final String LOGIN_URL = "user/login.html";
    //private static final String LIST_URL = "/user/list.html";


    private final Repository repository;
    private final Path homePath = Paths.get(ROOT_URL + HOME_URL);
    public RequestHandler(Socket connection){ // throws FileNotFoundException {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }


    @Override
    public void run() {
        //TODO의 실질적인 처리
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = new byte[0];      //왜 0?

            //Header분석
            //-> 입력받은 string에서 HTTP메서드(ex.get)와 url을 구분지어 저장하기 위해서 사용!!!
            // startLine은 http 요청 응답 메시지 중 시작줄을 의미한다!!
            // - 요청 시작줄 → 메서드 + 주소 + 버전 <<< ex : GET [url] HTTP/1.1 >>>

            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");     //띄어쓰기 구분해서 입력받아주는 메서드
            String method = startLines[0];  // method에는 http메서드를 저장
            String url = startLines[1];     // url에는 주소를 저장

            int requestContentLength = 0;
            String cookie = "";

            //요구사항3
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }

                if(line.startsWith("Cookie")){
                    body = Files.readAllBytes(Paths.get(ROOT_URL + url));
                }
            }
            /**
             * request body안의 정보를 읽어오기 위해서는 Header의 Content-Length 값이 필요하고,
             * BufferedReader의 offset을 body 값 바로 앞에 위치시켜야한다.
             * 이걸 구현한게 위 코드이다.
             */


            //요구사항1
            //body = Files.readAllBytes(pathIndex);

            //form.html까지 들어가는거 성공!!!
            if(method.equals("GET") && url.endsWith(".html")){
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }
            if(url.equals("/")){
                body = Files.readAllBytes(homePath);
            }

            //요구사항2
            /**
             * 2. 정보를 입력하면 쿼리스트링으로부터 MomoryUserRepository에 정보를 받아와서
             *    다시 index.html 화면을 띄운다
             */

            if(url.equals("/user/signup")){
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(queryString);      //queryString의 정보를 parsing하는 메서드
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
                //새로운 user객체 생성
                repository.addUser(user);
                //user객체의 정보를 MemoryUserRepository에 저장
                response302Header(dos, HOME_URL);
                //dos
                return;
            }

            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path){
        try{
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true" + "\r\n");
            dos.writeBytes("\r\n");
        }catch(IOException e){
            log.log(Level.SEVERE, e.getMessage());
        }

        /*
        private void response302HeaderWithCookie(DataOutputStream dos, String path){
            try{
                dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
                dos.writeBytes("Location: " + path + "\r\n");
                dos.writeBytes("Set-Cookie: logined=true" + "\r\n");

                dos.writeBytes("\r\n");
            }catch(IOException e){
                log.log(Level.SEVERE, e.getMessage());
            }
        }
         */

    }
}
