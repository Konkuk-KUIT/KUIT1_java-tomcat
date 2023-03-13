package db;

import model.User;

import java.util.Collection;

public interface Repository {
    void addUser(User user);
    User findUserById(String id);
    Collection<User> findAll();
}
