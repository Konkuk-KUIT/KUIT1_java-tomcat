package model;

import http.util.HttpRequestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import db.MemoryUserRepository;

import java.util.Collection;
import java.util.Map;

import static model.UserQueryKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("addUser: Repository에 반영되는지 확인")
    public void addUserTest(){
        User user = new User("uid12", "1234", "name", "ke123@gmail.com");

        MemoryUserRepository repo = MemoryUserRepository.getInstance();
        // Before: addUser
        Collection<User> userList = repo.findAll();
        assertTrue(userList.size() == 0);

        // After : addUser
        repo.addUser(user);
        userList = repo.findAll();
        assertTrue(userList.size() != 0);
    }

    @Test
    @DisplayName("addUser: query에서 parsing하여 생성")
    public void addUserTest_With_Query(){
        Map<String, String> queries = HttpRequestUtils.parseQueryParameter("userId=uid12&password=1234&name=tmp&email=tmp@gamil.com");


        User user = new User(queries.get("userId"), queries.get("password"), queries.get("name"), queries.get("email"));

        MemoryUserRepository repo = MemoryUserRepository.getInstance();
        repo.addUser(user);
        Collection<User> userList = repo.findAll();
        assertTrue(userList.size() != 0);

        User findUser = repo.findUserById(queries.get("userId"));
        assertEquals(findUser.getUserId(), "uid12");
    }

    @Test
    @DisplayName("addUser: UserQueryKey enum class 이용 ")
    public void addUserTest_WithUserQueryKey(){
        Map<String, String> queries = HttpRequestUtils.parseQueryParameter("userId=uid12&password=1234&name=tmp&email=tmp@gamil.com");

        assertEquals("uid12", queries.get(USERID.getKey()));

        User user = new User(queries.get(USERID.getKey()), queries.get(PASSWORD.getKey()), queries.get(NAME.getKey()), queries.get(EMAIL.getKey()));

        MemoryUserRepository repo = MemoryUserRepository.getInstance();
        repo.addUser(user);
        Collection<User> userList = repo.findAll();
        assertTrue(userList.size() != 0);

        User findUser = repo.findUserById(queries.get(USERID.getKey()));
        assertEquals(findUser.getUserId(), "uid12");
    }



}