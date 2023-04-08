package webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class WebServer {
    private static final int DEFAULT_PORT = 80;
    private static final int DEFAULT_THREAD_NUM = 50;
    private static final Logger log = Logger.getLogger(WebServer.class.getName());

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        //생성될 스레드의 개수를 제안함 -> 너무 많이 만들면 한번에 요청을 처리하다 서버가 다운될 수도 있으니까
        ExecutorService service = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);

        /*
        File file = new File("/Users/lyouxsun/Desktop/java-tomcat/webapp/index.html");
        FileInputStream output = new FileInputStream(file);
        */

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }
        //try-with-resource
        //-> try가 끝나자마자 (try에서) 사용한 resource들을 자동으로 반납한다

        // TCP 환영 소켓
        try (ServerSocket welcomeSocket = new ServerSocket(port)){
            // 연결 소켓f
            Socket connection;
            while ((connection = welcomeSocket.accept()) != null) {     //  while문을 통해 지속적인 통신이 가능하도록 만들기
                //input/outputStream과 관련된 기능은 RequestHandler의 run 메서드에서 구현
                // 스레드에 작업 전달
                service.submit(new RequestHandler(connection));

            }
        }

    }
}
