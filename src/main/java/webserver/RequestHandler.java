package webserver;

import db.MemoryUserRepository;
import http.util.IOUtils;
import model.User;

import javax.swing.text.Document;
import javax.swing.text.Element;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            //요구사항 1
            String url = br.readLine();
            String[] info = url.split(" ");
//            for (String i : info)
//                System.out.println(i);
            String type = info[0];

            System.out.println("info[1]"+info[1]);

            String filePath = info[1];


            if (url != null) {
                System.out.println("url : "+url);
            }

            byte[] index = new byte[0];
            //if(fileName.equals("index.html")) {
            if (filePath.equals("/index.html")) {
                try {
                    // read all bytes
                    //index = Files.readAllBytes(Paths.get("webapp/" + fileName));
                    index = Files.readAllBytes(Paths.get("webapp" + filePath));

                    // convert bytes to string
                    String content = new String(index, StandardCharsets.UTF_8);

                    // print contents
                    System.out.println(content);


                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }

            //요구사항 2-1
            if (filePath.equals("/user/form.html")) {
                try {
                    // read all bytes
                    index = Files.readAllBytes(Paths.get("webapp" + filePath));

                    // convert bytes to string
                    String content = new String(index, StandardCharsets.UTF_8);

                    // print contents
                    System.out.println(content);


                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }

//            byte[] body = "Hello World".getBytes();
//            response200Header(dos, body.length);
//            responseBody(dos, body);
            response200Header(dos, index.length);
            responseBody(dos, index);

    ;
            //요구사항 2-1: form에 적은 값 가져오기
            log.log(Level.INFO,"log찍기");
            String fileName = info[1];
            String SignUpInfoURL=info[1].split("\\?")[1];
            String[] SignUpInfos=SignUpInfoURL.split("\\&");
            String[] SignUpInfoValues=new String[4];
            for(int i=0;i<4;i++) {
                SignUpInfoValues[i]=SignUpInfos[i].split("\\=")[1];
            }
            User user1=new User(SignUpInfoValues[0],SignUpInfoValues[1],SignUpInfoValues[2],SignUpInfoValues[3]);
            MemoryUserRepository memoryUserRepository1=MemoryUserRepository.getInstance();
            memoryUserRepository1.addUser(user1);
            System.out.println("137pjy name:"+memoryUserRepository1.findUserById("137pjy").getName());
            System.out.println("135psj name:"+memoryUserRepository1.findUserById("135psj").getName());
            log.log(Level.INFO,"log찍기");

            if (filePath.contains("/user/signup")) {


            }

            //요구사항 3
            //                    String signUpContentLength="";
//                    while((tmp = br.readLine())!=null){
//                        System.out.println("tmp:"+tmp);
//                        if(tmp.startsWith("Content-Length")){
//                            signUpContentLength=tmp;
//                        }
//                        int signUpContentLengthInt=Integer.parseInt(signUpContentLength);
//                        String queryString = IOUtils.readData(br, signUpContentLengthInt);
//                        System.out.println(queryString);
//                        Map<String, String> queryParameter = parseQueryParameter(queryString);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }

    }

    //요구사항 2-1 : form에 적은 값 가져오기
//        if (url.equals("/user/signup")) {
//            String queryString = IOUtils.readData(br, requestContentLength);
//            Map<String, String> queryParameter = parseQueryParameter(queryString);
//            User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
//            repository.addUser(user);
//            response302Header(dos,HOME_URL);
//            return;
//        }




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
