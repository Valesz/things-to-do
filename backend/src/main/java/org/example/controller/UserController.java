package org.example.controller;

import java.util.Objects;
import org.example.MyConfiguration;
import org.example.model.User;
import org.example.model.listing.UserListingResponse;
import org.example.service.UserService;
import org.example.utils.enums.UserStatusEnum;
import org.example.utils.exceptions.ServiceException;
import org.example.utils.exceptions.ServiceExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/user")
@Import(value = {MyConfiguration.class})
public class UserController
{

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public User addUser(@RequestBody User user)
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
				case NULL_ARGUMENT, ID_GIVEN, INVALID_ARGUMENT:
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

				case CONSTRAINT_VIOLATION:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@RequestMapping(value = "/", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public User updateUser(@RequestBody User user,
		@RequestHeader("Authorization") String token)
	{
		try
		{
			User requestedUser = userService.getUserByToken(token.substring(7));

			if (user.getId() != null && userService.getUserById(user.getId()) != null
				&& !Objects.equals(requestedUser.getId(), user.getId()))
			{
				throw new ServiceException(ServiceExceptionType.UNAUTHORIZED_TO_CHANGE,
					"Changing other user's data is not allowed!"
				);
			}

			User savedUser = userService.updateUser(user);
			savedUser.setPassword(null);
			return savedUser;
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case ID_NOT_GIVEN, INVALID_ARGUMENT:
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

				case UNAUTHORIZED_TO_CHANGE:
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());

				case CONSTRAINT_VIOLATION:
					throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());

				case ID_NOT_FOUND:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public UserListingResponse listUsers(@RequestParam(value = "id", required = false) Long id,
		@RequestParam(value = "username", required = false) String username,
		@RequestParam(value = "email", required = false) String email,
		@RequestParam(value = "timeofcreation", required = false) String timeofcreation,
		@RequestParam(value = "status", required = false) UserStatusEnum status,
		@RequestParam(value = "classification", required = false) Double classification,
		@RequestParam(value = "precisionofanswers", required = false) Double precisionofanswers,
		@RequestParam(value = "pagenumber", required = true) long pageNumber,
		@RequestParam(value = "pagesize", required = true) long pageSize)
	{
		User filter = new User(id,
			username != null && !username.isEmpty() ? username : null,
			email != null && !email.isEmpty() ? email : null,
			timeofcreation != null && !timeofcreation.isEmpty() ? LocalDate.parse(timeofcreation) : null,
			status,
			null,
			classification,
			precisionofanswers);

		return new UserListingResponse(
			userService.getByUsersObject(filter, pageNumber, pageSize),
			userService.getByUsersObjectCount(filter)
		);
	}

	@RequestMapping(value = "/token", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public User getUserByToken(@RequestHeader(value = "Authorization") String token)
	{
		return userService.getUserByToken(token.substring(7));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable("id") Long id,
		@RequestHeader(value = "Authorization") String token)
	{
		try
		{
			User requestedUser = userService.getUserByToken(token.substring(7));

			if (userService.getUserById(id) != null && !Objects.equals(requestedUser.getId(), id))
			{
				throw new ServiceException(ServiceExceptionType.UNAUTHORIZED_TO_CHANGE,
					"Deleting other user's data is not allowed!"
				);
			}

			userService.deleteUser(id);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case UNAUTHORIZED_TO_CHANGE:
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());

				case ID_NOT_FOUND:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
