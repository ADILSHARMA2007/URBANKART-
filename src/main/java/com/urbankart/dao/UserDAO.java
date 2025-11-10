package com.urbankart.dao;

import com.urbankart.model.User;
import java.util.List;

public interface UserDAO {
    User getUserById(int id);
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    boolean createUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int id);
    boolean authenticateUser(String username, String password);
}