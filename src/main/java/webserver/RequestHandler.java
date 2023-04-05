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
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            //첫줄 읽어서 방식, 경로, 버전 확인
            String restfulAPI = br.readLine();
            System.out.println(restfulAPI);

            StringTokenizer st = new StringTokenizer(restfulAPI, " ");

            String restfulMethod = st.nextToken();
            String restfulPath = st.nextToken();
            String restfulVersion = st.nextToken();

            int requestContentLength = 0;
            boolean cookie = false;
            while (true) {
                final String line = br.readLine();
                System.out.println("line : " + line);
                if (line.equals("")) break;
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                // header info
                if (line.startsWith("Cookie")) {
                    if (line.split(":")[1].contains("logined=true")) {
                        cookie = true;
                    }
                }

            }

            System.out.println("restfulPath : " + restfulPath);
            if(restfulMethod.equals("GET")) processGet(dos, restfulPath, cookie);
            if(restfulMethod.equals("POST")) processPost(dos, br, restfulPath, requestContentLength);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    //Post방식 처리
    private void processPost(DataOutputStream dos,BufferedReader br, String restfulPath, int requestContentLength) throws IOException {

        //body 있으면?
        if(requestContentLength != 0) {
            //body 가져오기
            String body = IOUtils.readData(br, requestContentLength);
            System.out.println("Body : " + body);
            Map<String, String> map = HttpRequestUtils.parseQueryParameter(body);
            if(restfulPath.equals("/user/signup")) signUp(dos, map);

            if(restfulPath.equals("/user/login")) Login(dos, map);
        }
    }



    //get 요청이 들어올 시 해당 내용 처리
    private void processGet(DataOutputStream dos, String restfulPath, boolean cookie) throws IOException {
        StringTokenizer st;
        //query가 있는지 없는지?
        st = new StringTokenizer(restfulPath, "?");
        String filePath = st.nextToken();

        // "/" 요청시 index.html로 전환
        if(filePath.equals("/")) filePath = "/index.html";

        //단순 경로 요청 시?
        if(filePath.contains(".html"))
            getPathFile(filePath, dos, 200, false, false);

        //css가 있을 시?
        if(filePath.contains(".css"))
            getPathFile(filePath, dos, 200, false, true);

        //query가 있을 시? //? 체크해서 값 얻어냄
        if(st.hasMoreTokens()) {
            //map에 모든 해당 값을 넣어주고
            Map<String, String> map = HttpRequestUtils.parseQueryParameter(st.nextToken());
            getCheckPath(dos, filePath, map, false);
        }

        //단순 경로 요청도 아니고 query도 없을 시
        else {
            getCheckPath(dos, filePath, null, cookie);
        }
    }

    //Get 방식일 때 경로 파악후 알맞은 함수로 보내줌
    private void getCheckPath(DataOutputStream dos, String filePath, Map<String, String> map, boolean cookie) throws IOException {
        if(filePath.equals("/user/signup")) signUp(dos, map);
        if(filePath.equals("/user/userList")) showList(dos, cookie);

    }

    //showList controller
    private void showList(DataOutputStream dos, boolean cookie) throws IOException {
        if(cookie) {
            getPathFile("/user/list.html", dos, 302, cookie, false);
            return;
        }
        getPathFile("/user/login.html", dos, 302, cookie,false);
    }

    //signUp Controller
    private void signUp(DataOutputStream dos, Map<String, String> map) throws IOException {

            repository.addUser(new User(map.get("userId"),
                    map.get("password"),
                    map.get("name"),
                    map.get("email")));

            System.out.println("회원가입 완료");
            getPathFile("/index.html", dos, 302, false,false );
    }


    //Login Controller
    private void Login(DataOutputStream dos, Map<String, String> map) throws IOException {
        User user = repository.findUserById(map.get("userId"));
        System.out.println("user : " + user);
        //아이디 비번 같을 시 index.html 출력
        if((user != null) && (user.getPassword().equals(map.get("password")))) {
            getPathFile("/index.html", dos, 302, true, false);
            return;
        }
        //다를 시 login_failed.html 출력
        getPathFile("/user/login_failed.html", dos, 302, false, false);

    }


    //파일 경로를 읽어 출력
    private void getPathFile(String filePath, DataOutputStream dos, int status, boolean cookie, boolean css) throws IOException {

        String fullpath = "webapp" + filePath;

         FileInputStream fis = new FileInputStream(fullpath);
        byte[] fisBody = fis.readAllBytes();
        if(status == 200)
            response200Header(dos, fisBody.length, css);
        if(status == 302)
        {
            if(cookie)response302Header(dos, fisBody.length, filePath,  true);
            else response302Header(dos, fisBody.length, filePath, false);
        }
            responseBody(dos, fisBody);
    }

    private void response302Header(DataOutputStream dos, int lengthOfBodyContent, String filePath, boolean cookie) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");

        dos.writeBytes("Content-Type: text/html;charset=utf-8 \r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        if(cookie) dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
        dos.writeBytes("Location: " + filePath + "\r\n");
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, boolean css) throws IOException {

        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        if(css) dos.writeBytes("Content-Type: text/css,*/*;q=0.1 \r\n");
        else dos.writeBytes("Content-Type: text/html;charset=utf-8 \r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
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
