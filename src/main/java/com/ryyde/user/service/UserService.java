package com.ryyde.user.service;

import java.util.List;

import com.ryyde.user.model.User;

public interface UserService {

    User createUser(User user);

    User getUserById(String id);

    List<User> getAllUsers();

    User updateUser(String id, User user);

    void deleteUser(String id);

    User getUserByEmail(String email);
}
