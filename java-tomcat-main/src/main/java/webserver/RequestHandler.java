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


//회원가입을 get방식으로 할 땐 content length가 없어서 오류 뜸.
public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String ROOT_URL = "./webapp";
    private static final String HOME_URL = "/index.html";
    private static final String LOGIN_FAILED_URL = "/user/login_failed.html";
    private static final String LOGIN_URL = "/user/login.html";
    private static final String LIST_URL = "/user/list.html";

    private final Repository repository;
    private final Path homePath = Paths.get(ROOT_URL + HOME_URL);

    public RequestHandler(Socket connection) {

        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        //실질적인 처리를 담당하는 부분.
        //trywithresource를 이용해 connection에 inputstream 또한 자원이기 때문에 자원 사용후 반납해주도록 함.
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = new byte[0];

            //Header를 먼저 써주고 Body를 써주기

            //연결 소켓으로부터 받아온 inputstream과 outstream의 데이터를 써주면
            //클라이언트에 알맞은 응답이 갈 것.


            // Header 분석
            //packet의 헤더에 해당하는 부분을 읽어오는 것. 각 줄을 읽었을 때 줄이 비어있으면 반복문을 빠져 나감(끝까지 다 읽었을 경우)/contentLength는 Post형식으로 Body가 있을 경우 Body의 길이를 의미함.

            String startLine = br.readLine();  //맨 첫 줄 [어떤 방식으로 가져오로 거고]/url 을 읽어올 때 사용
            String[] startLines = startLine.split(" "); //Start line을 공백을 기준으로 쪼개고 string에 배열 형식으로 저장 예를 들어 get/http/ 등으로 끊기는 것.
            String method = startLines[0]; // 가지고 오는 방식 ex. post, get, delete,patch, put 등
            String url = startLines[1]; // url이 적힌 부분

            int requestContentLength = 0;
            String cookie = "";

            while (true) { //무한반복
                //br.readLine으로 한 줄씩 읽고, 이걸 line에 저장.
                //contentLength를 함께 넘겨겨주어 broken pipe문제를 막아줌. 즉, body의 content-length만큼을 읽어와서 /r/n으로 끝나도 헛돌지 않게함.
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) { //body길이 정보가 있으면 가져오고, value부분을 requstContentlength에 저장.
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }

                if (line.startsWith("Cookie")) { // cookie정보가 있으면 가져와라. 단, :기준으로 쪼개어서 가져와주기. 보통은 이름:내용 방식으로 있으니까 [1]을 하여 value 부분을 cookie에 저장하라
                    cookie = line.split(": ")[1];
                }
            }

            // 요구 사항 1번
            //method http가 get방식이고, 파일이 .html로 끝나면 (우리는 index.html을 요구할 것이니까 이 조건에 해당함) webapp/index.html로 들어가서, html 문서 전체 내용을 byte형태로 읽어온 후 body에 저장해라.
            if (method.equals("GET") && url.endsWith(".html")) {
                body = Files.readAllBytes(Paths.get(ROOT_URL + url));
            }

            // / 는 기본 폴더를 의미하는데, 여기선 java tomcat을 의미. /를 요구하면 기본으론 자바 톰캣으로 들어오게 되는데 우린 index.html이 나오길 바라니 body에 index.html로 바꿔라.

            if (url.equals("/")) {
                body = Files.readAllBytes(homePath);
            }

            // 요구 사항 2,3,4번

            if (url.equals("/user/signup")) {
                String queryString = IOUtils.readData(br, requestContentLength);  //contentlength만큼 body 데이터를 읽어오기. 그리고 그것을 String으로 반환하여 queryString에 저장
                //map은 key값과 value값이 1:1로 연결되고, key값은 중복 불가. value는 중복 가능한 자료구조.
                Map<String, String> queryParameter = parseQueryParameter(queryString); //map 구조를 사용해줄 것이란 의미 (ex. userID: kihwn) 그리고 그걸 queryParameter 넣어주기.
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email")); //이 방식으로 맵 저장.
                repository.addUser(user); // 위 유저데이터를 repository에 저장해줌.
                response302Header(dos,HOME_URL);  //그 다음에 Home_URL(index.html)로 다시 요청하라는 뜻. 근데 왜 200Header를 안쓰고 redirect를 썼지? 왜냐하면 redirect를 안해주면 회원가입이 끝나고 url이 user/signup이라고 뜨면서 홈화면으로 돌아가고
                //거기서 다시 회원가입을 누르면 user/user/signup이 뜨기 때문. 즉, redirect를 안하면 서버 내부적으로 처리되는 느낌(client에게 요청이 가지않고. 그래서 client에게 보여지는 url이 바뀌지 않음.).
                return;
            }

            // 요구 사항 5번
            //redirect는 요청을 다시 하라는 의미. 예를 들어 브라우저가 A URL을 요구했을 때 서버가  B URL(location)로 다시 요청해봐라고 하는 것이 리다이렉트.
            // 보통 redirect는 유저 정보를 볼 때 로그인이 안되어 있는 경우 유저 정보를 볼 수 없기 때문에 서버 측에서 로그인 페이지를 리다이렉트 시켜줌. 그것을 클라이언트 측에선 302코드를 보고 리다이렉트 처리했음을 알게됨.
            //B URL은 보통 Location에 담겨 보내지고, 그렇다면 브라우저는 location 으로 보내달라고 요구하게 됨.
            if (url.equals("/user/login")) {
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                User user = repository.findUserById(queryParameter.get("userId"));
                login(dos, queryParameter, user);
                return;
            }

            // 요구 사항 6번
            //user/userList 요청이 들어오면  cookie를 확인해서 로그인 되었나 확인하고 로그인 안되어있으면 로그인 창으로, 되어있으면 list 보여주는 부분.
            //
            if (url.equals("/user/userList")) {
                if (!cookie.equals("logined=true")) {
                    response302Header(dos,LOGIN_URL);
                    return;
                }
                body = Files.readAllBytes(Paths.get(ROOT_URL + LIST_URL));
            }

            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void login(DataOutputStream dos, Map<String, String> queryParameter, User user) {
        if (user != null && user.getPassword().equals(queryParameter.get("password"))) {
            response302HeaderWithCookie(dos,HOME_URL);
            return;
            //user 아이디가 없지 않은 경우(not null), 그리고 그 userid가 queryparameter가 일치한다면, 홈화면으로 redirect로 쿠키와 함께 보내기.
        }
        response302Header(dos,LOGIN_FAILED_URL);
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

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true" + "\r\n");

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
