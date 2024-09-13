package org.example.service.impl;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource(name = "userRepository")
    UserRepository userRepository;

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Deletion of " + id + " user failed: " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteAll() {
        try {
            userRepository.deleteAll();
        } catch (Exception e) {
            System.out.println("Deletion of all users failed: " + e.getMessage());
            return false;
        }

        return true;
    }

}
