package org.example.service;

import org.example.model.User;
import org.example.utils.exceptions.ServiceException;

public interface UserService {

    Iterable<User> getAllUsers();

    User getUserById(Long id);

    Iterable<User> getByUsersObject(User user);

    User saveUser(User user) throws ServiceException;

    User updateUser(User user) throws ServiceException;

    void deleteUser(Long id);

    void deleteAll();

}
