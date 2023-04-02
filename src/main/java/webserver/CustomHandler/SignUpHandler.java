package webserver.CustomHandler;

import db.MemoryUserRepository;
import model.User;

import java.io.IOException;
import java.util.Map;

public class SignUpHandler implements CustomHandler {

    private final MemoryUserRepository repository;

    public SignUpHandler() {
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public byte[] process(Map<String, String> paramMap) throws IOException {
        User user = new User(
                paramMap.get("userId"),
                paramMap.get("password"),
                paramMap.get("name"),
                paramMap.get("email")
        );

        repository.addUser(user);

        IndexHandler indexHandler = new IndexHandler();
        return indexHandler.process(paramMap);
    }
}
