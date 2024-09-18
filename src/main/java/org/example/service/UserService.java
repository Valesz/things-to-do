package org.example.service;

import org.example.model.User;

public interface UserService {

    Iterable<User> getAllUsers();

    User getUserById(Long id);

    Iterable<User> getByUsersObject(User user);

    User saveUser(User user);

    boolean deleteUser(Long id);

    boolean deleteAll();

}
