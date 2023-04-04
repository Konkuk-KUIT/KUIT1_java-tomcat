package webserver;

import java.io.IOException;
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

        // 서버가 한번에 처리할 수 있는 스레드의 개수 제한
        // 50개까지의 요청은 스레드에서 돌아가고, 그 이상의 요청들은 앞에서 대기하도록 한다.
        ExecutorService service = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        // try-with-resource -> try문이 끝나자마자 () 안의 자원을 반납함
        // TCP 환영 소켓
        try (ServerSocket welcomeSocket = new ServerSocket(port)){  // client와 소통하기 위한 port번호가 80인 서버 소켓을 열음

            // client와 80번 포트의 애플리케이션이 한번 연결되면, 서로 연결되었음이 확인된 상태이므로 새로운 포트번호를 가진 소켓을 만들어서 또 연결
            // 80번 포트가 모든 요청을 처리하면, 시간이 오래걸리는 요청이 왔을 때 병목현상이 발생할 수 있기 때문에
            // 실질적으로 clinet sercer 간 데이터를 주고 받을 수 있는 연결 소켓을 새로 만듦

            // 연결 소켓
            Socket connection;
            while ((connection = welcomeSocket.accept()) != null) {  // 서버 소켓을 통해 요청 accept
                // 스레드에 작업 전달
                service.submit(new RequestHandler(connection)); // RequestHandler의 run메서드가 실행됨
            }
        }

    }
}
