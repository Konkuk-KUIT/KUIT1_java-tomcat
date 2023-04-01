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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;


import java.util.Map.Entry;
import java.util.stream.Collectors;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            final String homePath = "./webapp";
            final String homeUrl = "/index.html";
            final Repository repository;

            String startLines[] = br.readLine().split(" ");
            String method = startLines[0];
            String url = startLines[1];
            String type = "";
            int requestContentLength = 0;

            byte[] body = new byte[0];

            // TODO: method & url 출력문 제거
            System.out.println(method +"\t"+ url);
            while(true){
                String temp = br.readLine();
                if(temp.equals("")) break;
                if(temp.startsWith("Content-Length")){
                    temp = temp.split(": ")[1];
                    requestContentLength = Integer.parseInt(temp);
                }
                System.out.println(temp);
            }

            // 요구사항 1 - 홈 화면 index.html 반환
            if (method.equals("GET") && url.endsWith(".html")){
                body = Files.readAllBytes(new File(homePath+url).toPath());
                type = "html";
            }
            if(url.equals("/")){
                body = Files.readAllBytes(new File(homePath+homeUrl).toPath());
                type = "html";
            }

            // 요구사항 7 - css 적용
            if (url.endsWith(".css")){
                if (url.startsWith("/user"))
                    url = url.substring(5);
                body = Files.readAllBytes(new File(homePath+url).toPath());
                type = "css";
            }

            // 요구사항 2/3 - GET/POST 방식으로 회원가입하기
            if (url.equals("/user/signup")){
                String queryString = IOUtils.readData(br, requestContentLength);
                Map<String, String> queryData = parseQueryString(queryString);

                repository = MemoryUserRepository.getInstance();
                User user = new User(queryData.get("userId"), queryData.get("password"), queryData.get("name"), queryData.get("email"));
                repository.addUser(user);

                body = Files.readAllBytes(new File(homePath+homeUrl).toPath());
                type = "html";
            }

            response200Header(dos, body.length, type);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryMap = new HashMap<>();

        String[] queryStrings = queryString.split("&");
        for(String tempQueryString : queryStrings){
            System.out.println(tempQueryString);
            String[] query = tempQueryString.split("=");
            queryMap.put(query[0], query[1]);
        }

        return queryMap;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String type) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/"+ type + ";charset=utf-8\r\n");
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
