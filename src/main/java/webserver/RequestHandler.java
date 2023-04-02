package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    MemoryUserRepository repository = MemoryUserRepository.getInstance();

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            //in은 Header만..?
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            //첫줄 읽어서 방식, 경로, 버전 확인
            String restfulAPI = br.readLine();
            System.out.println(restfulAPI);
           // System.out.println(br.readLine());

            StringTokenizer st = new StringTokenizer(restfulAPI, " ");

            String restfulMethod = st.nextToken();
            String restfulPath = st.nextToken();
            String restfulVersion = st.nextToken();

            System.out.println("restfulPath : " + restfulPath);
            if(restfulMethod.equals("GET")) processGet(dos, restfulPath);
            if(restfulMethod.equals("POST")) {
                processPost(dos, br, restfulPath);

            }

            // byte[] body = "Hello World".getBytes();
           // response200Header(dos, body.length);
            // responseBody(dos, body);

            //System.out.println(repository.findAll());
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    //Post방식 처리
    private void processPost(DataOutputStream dos,BufferedReader br, String restfulPath) throws IOException {

        int requestContentLength = 0;
        while (true) {
            final String line = br.readLine();
            System.out.println("line : " + line);
            if (line.equals("")) break;
            // header info
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }

        }
        //body 있으면?
        if(requestContentLength != 0) {
            //body 가져오기
            String body = IOUtils.readData(br, requestContentLength);
            System.out.println("Body : " + body);
            Map<String, String> map = HttpRequestUtils.parseQueryParameter(body);
            if(restfulPath.equals("/user/signup")) Signup(dos, map);
        }
    }

    //get 요청이 들어올 시 해당 내용 처리
    private void processGet(DataOutputStream dos, String restfulPath) throws IOException {
        StringTokenizer st;
        //query가 있는지 없는지?
        st = new StringTokenizer(restfulPath, "?");
        String filePath = st.nextToken();

        // "/" 요청시 index.html로 전환
        if(filePath.equals("/")) filePath = "/index.html";

        //단순 경로 요청 시?
        if(filePath.contains(".html")) getPathFile(filePath, dos);

        //query가 있을 시? //? 체크해서 값 얻어냄
        if(st.hasMoreTokens()) {
            //map에 모든 해당 값을 넣어주고
            Map<String, String> map = HttpRequestUtils.parseQueryParameter(st.nextToken());
            getCheckPath(dos, filePath, map);
        }
    }

    //경로 파악후 알맞은 함수로 보내줌
    private void getCheckPath(DataOutputStream dos, String filePath, Map<String, String> map) throws IOException {
        if(filePath.equals("/user/signup")) {
            Signup(dos, map);
        }
    }

    //회원가입이 들어왔을 시 회원가입
    private void Signup(DataOutputStream dos, Map<String, String> map) throws IOException {

            repository.addUser(new User(map.get("userId"),
                    map.get("password"),
                    map.get("name"),
                    map.get("email")));

            System.out.println("회원가입 완료");
            getPathFile("/index.html", dos);
    }


    //파일 경로를 읽어 출력
    private void getPathFile(String filePath, DataOutputStream dos) throws IOException {

        String fullpath = "webapp" + filePath;

         FileInputStream fis = new FileInputStream(fullpath);
        byte[] fisBody = fis.readAllBytes();
        response200Header(dos, fisBody.length);
        responseBody(dos, fisBody);
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

}
