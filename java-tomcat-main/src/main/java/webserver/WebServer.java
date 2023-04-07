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
        //스레드를 만들게 되면 예를 들어 FIFO 형태로 요청을 처리하다 보면 앞 순서에 시간이 많이 잡아먹는 요청이 있고, 뒷 순서에 금방 끝나는 요청이 있으면 병목 현상이 생기게됨. 이를 해소하기 위해 스레드처리.
        // 밑에 코드의 의미는 Default_Thread_Num까지는 스레드는 그대로 만들고, 그 이상의 요청은 앞에서 대기할 수 있도록 하는 것.
        int port = DEFAULT_PORT;
        ExecutorService service = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        // 내컴퓨터에서 소켓을 통해 클라이언트와 통신을 하게 됨. 즉, 어플리케이션의 주소인 포트번호를 찾아오면
        // 소켓을 통해 입장하게 되는 것.
        // 소켓은 자원을 사용하는 것이라 자원을 사용한 다음에는 자원을 반납해줘야 함. 이 때 try - with - resource를 사용
        // try-with-resourece를 사용하면 자원을 사용하고 나면 반납 처리를 해줌.
        try (ServerSocket welcomeSocket = new ServerSocket(port)){
            // Try 소켓은 TCP 환영 소켓. 환영 소켓은 첫 문을 열어주는 역할.

            // 실질적인 연결 소켓. Try 소켓만 활용할 경우엔 병목 현상이 일어날 수 있어 연결 소켓을 새로 만들어줌.
            //즉, 실질적으로 클라이언트와 서버가 데이터를 주고받는 소켓은 연결 소켓.
            Socket connection; // 이것을 RequestHandler에도 끌고 가 인스턴스 변수로 선언.
            while ((connection = welcomeSocket.accept()) != null) {
                //while문을 통해 지속적인 통신을 가능하게 함. 첫 요청이 들어오면 while문에 커넥션이 실행되고, accept가 실행되고 그 이후 또 요청이 들어와도 같은 방식으로 처리해줌.
                // 스레드에 작업 전달
                service.submit(new RequestHandler(connection));
                //여기까지 오면 RequestHandler에서 런처리 해줌. 실질적인 처리를 Request handler에서 하면 됨.
            }
        }

    }
}
