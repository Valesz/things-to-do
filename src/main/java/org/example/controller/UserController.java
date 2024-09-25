package org.example.controller;

import org.example.model.User;
import org.example.service.UserService;
import org.example.utils.exceptions.ServiceException;
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
        } catch (ServiceException e) {
            switch (e.getServiceExceptionTypeEnum()) {
                case NULL_ARGUMENT:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

                case ILLEGAL_ID_ARGUMENT:
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());

                default:
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public User updateUser(@RequestBody User user) {
        try {
            User savedUser = userService.updateUser(user);
            savedUser.setPassword(null);
            return savedUser;
        } catch (ServiceException e) {
            switch (e.getServiceExceptionTypeEnum()) {
                case NULL_ARGUMENT:
                case CONSTRAINT_VIOLATION:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

                case ILLEGAL_ID_ARGUMENT:
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

                default:
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Iterable<User> listUsers(@RequestBody(required = false) User user) {
        return userService.getByUsersObject(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
