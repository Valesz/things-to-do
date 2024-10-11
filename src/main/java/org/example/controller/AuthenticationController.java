package org.example.controller;

import org.example.model.User;
import org.example.service.UserService;
import org.example.service.impl.AuthenticationServiceImpl;
import org.example.service.impl.JwtServiceImpl;
import org.example.utils.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController
{
	@Autowired
	private JwtServiceImpl jwtService;

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationServiceImpl authenticationService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public User register(@RequestBody User user)
	{
		try
		{
			User savedUser = userService.saveUser(user);
			savedUser.setPassword(null);
			return savedUser;
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case ID_GIVEN:
				case NULL_ARGUMENT:
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

				case CONSTRAINT_VIOLATION:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public String authenticate(@RequestBody User user)
	{
		User authenticatedUser = authenticationService.authenticate(user.getUsername(), user.getPassword());

		return jwtService.generateToken(authenticatedUser);
	}
}
