package org.example.service;

import org.example.model.User;
import org.example.utils.exceptions.NullValueException;

public interface UserService {

    Iterable<User> getAllUsers();

    User getUserById(Long id);

    Iterable<User> getByUsersObject(User user);

    User saveUser(User user) throws NullValueException;

    User updateUser(User user) throws NullValueException;

    boolean deleteUser(Long id);

    boolean deleteAll();

}
