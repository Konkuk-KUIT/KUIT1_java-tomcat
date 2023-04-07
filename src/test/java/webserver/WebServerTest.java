package webserver;

import db.MemoryUserRepository;
import db.Repository;
import model.User;
import org.junit.jupiter.api.Test;

public class WebServerTest {
    private static final Repository userRepository = MemoryUserRepository.getInstance();
    @Test
    void postSignUp(){
        User user = new User("testId","testPassword","testName","test@naver.com");
        userRepository.addUser(user);

    }
}
