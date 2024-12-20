package org.example.service;

import org.example.model.User;
import org.example.utils.exceptions.ServiceException;
import org.springframework.stereotype.Service;

@Service
public interface UserService
{

	Iterable<User> getAllUsers();

	User getUserById(Long id);

	User getUserByToken(String token);

	long getByUsersObjectCount(User user);

	Iterable<User> getByUsersObject(User user, long pageNumber, long pageSize);

	User saveUser(User user) throws ServiceException;

	User updateUser(User user) throws ServiceException;

	void deleteUser(Long id);

	void deleteAll();
}
