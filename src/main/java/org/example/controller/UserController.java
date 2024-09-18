package org.example.controller;

import org.example.model.User;
import org.example.service.UserService;
import org.example.utils.exceptions.NullValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody User user) {
        try {
            User savedUser = userService.saveUser(user);
            savedUser.setPassword("");
            return savedUser;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (NullValueException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public User updateUser(@RequestBody User user) {
        try {
            User savedUser = userService.updateUser(user);
            savedUser.setPassword("");
            return savedUser;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (NullValueException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Iterable<User> listUsers(@RequestBody(required = false) User user) {
        return userService.getByUsersObject(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean deleteUser(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }

}
