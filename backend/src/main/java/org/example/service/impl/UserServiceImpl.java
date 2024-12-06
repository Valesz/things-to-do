package org.example.service.impl;

import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.utils.enums.UserStatusEnum;
import org.example.utils.exceptions.ServiceException;
import org.example.utils.exceptions.ServiceExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService
{

	@Autowired
	UserRepository userRepository;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private JwtServiceImpl jwtService;

	private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,20}$";

	private static final String EMAIL_REGEX = "[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

	@Override
	public Iterable<User> getAllUsers()
	{
		return userRepository.findAll();
	}

	@Override
	public User getUserById(Long id)
	{
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public User getUserByToken(String token)
	{
		return getByUsersObject(User.builder().username(jwtService.extractUsername(token)).build()).iterator().next();
	}

	@Override
	public Iterable<User> getByUsersObject(User user)
	{

		if (user == null)
		{
			return userRepository.findAll();
		}

		SqlParameterSource namedParams = new MapSqlParameterSource()
			.addValue("id", user.getId())
			.addValue("username", user.getUsername())
			.addValue("email", user.getEmail())
			.addValue("timeofcreation", user.getTimeofcreation() == null ? null : user.getTimeofcreation().toString())
			.addValue("status", user.getStatus() == null ? null : user.getStatus().toString())
			.addValue("password", user.getPassword())
			.addValue("classification", user.getClassification())
			.addValue("precisionofanswers", user.getPrecisionofanswers());

		String query = constructQueryByOwnObject(user);

		return namedParameterJdbcTemplate.query(query, namedParams, rs ->
		{
			List<User> userList = new ArrayList<>();

			while (rs.next())
			{
				userList.add(User.builder()
					.id(rs.getLong("id"))
					.username(rs.getString("username"))
					.email(rs.getString("email"))
					.timeofcreation(LocalDate.parse(rs.getString("timeofcreation")))
					.status(UserStatusEnum.valueOf(rs.getString("status")))
					.password(rs.getString("password"))
					.classification(rs.getDouble("classification"))
					.precisionofanswers(rs.getDouble("precisionofanswers"))
					.build()
				);
			}

			return userList;
		});
	}

	private String constructQueryByOwnObject(User user)
	{
		StringBuilder query = new StringBuilder(" SELECT * FROM \"user\" ");

		query.append(" WHERE 1 = 1 ");

		if (user.getId() != null)
		{
			query.append(" AND id = :id ");
		}

		if (user.getUsername() != null)
		{
			query.append(" AND username = :username ");
		}

		if (user.getEmail() != null)
		{
			query.append(" AND email = :email ");
		}

		if (user.getTimeofcreation() != null)
		{
			query.append(" AND timeofcreation = :timeofcreation ");
		}

		if (user.getStatus() != null)
		{
			query.append(" AND status = :status ");
		}

		if (user.getClassification() != null)
		{
			query.append(" AND classification = :classification ");
		}

		if (user.getPrecisionofanswers() != null)
		{
			query.append(" AND precisionofanswers = :precisionofanswers ");
		}

		return query.toString();
	}

	@Override
	public User saveUser(User user) throws ServiceException
	{
		if (user.getId() != null)
		{
			throw new ServiceException(ServiceExceptionType.ID_GIVEN,
				"Remove id property, or use Update instead of Save."
			);
		}

		if (userRepository.findByUsername(user.getUsername()).isPresent())
		{
			throw new ServiceException(ServiceExceptionType.CONSTRAINT_VIOLATION,
				"User with given username already exists."
			);
		}

		validateUserProperties(user);

		if (!Pattern.matches(PASSWORD_REGEX, user.getPassword()))
		{
			throw new ServiceException(ServiceExceptionType.INVALID_ARGUMENT, "Invalid password");
		}

		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		return userRepository.save(user);
	}

	@Override
	public User updateUser(User user) throws ServiceException
	{
		if (user.getId() == null)
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_GIVEN,
				"Id field must not be null"
			);
		}

		if (!userRepository.existsById(user.getId()))
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_FOUND,
				"User with id " + user.getId() + " doesn't exist. Please use save to save this instance."
			);
		}

		User userInDatabase = userRepository.findByUsername(user.getUsername()).orElse(null);

		if (userInDatabase != null && !Objects.equals(userInDatabase.getId(), user.getId()))
		{
			throw new ServiceException(ServiceExceptionType.CONSTRAINT_VIOLATION,
				"User with given username already exists."
			);
		}

		User newUser = setNullProperties(user);

		validateUserProperties(newUser);

		return userRepository.save(newUser);
	}

	private User setNullProperties(User user)
	{
		User userInDb = userRepository.findById(user.getId()).orElse(new User());

		user.setUsername(user.getUsername() == null ? userInDb.getUsername() : user.getUsername());

		user.setEmail(user.getEmail() == null ? userInDb.getEmail() : user.getEmail());

		user.setTimeofcreation(user.getTimeofcreation() == null ? userInDb.getTimeofcreation() : user.getTimeofcreation());

		user.setStatus(user.getStatus() == null ? userInDb.getStatus() : user.getStatus());

		user.setPassword(user.getPassword() == null ? userInDb.getPassword() : bCryptPasswordEncoder.encode(user.getPassword()));

		user.setClassification(user.getClassification() == null ? userInDb.getClassification() : user.getClassification());

		user.setPrecisionofanswers(user.getPrecisionofanswers() == null ? userInDb.getPrecisionofanswers() : user.getPrecisionofanswers());

		return user;
	}

	private void validateUserProperties(User user) throws ServiceException
	{
		String errorMessage = checkForNullProperties(user);

		if (StringUtils.isNotBlank(errorMessage))
		{
			throw new ServiceException(ServiceExceptionType.NULL_ARGUMENT, errorMessage);
		}

		if (!Pattern.matches(EMAIL_REGEX, user.getEmail()))
		{
			throw new ServiceException(ServiceExceptionType.INVALID_ARGUMENT, "Invalid email address");
		}
	}

	private String checkForNullProperties(User user)
	{
		StringBuilder sb = new StringBuilder();

		if (StringUtils.isBlank(user.getUsername()))
		{
			sb.append("Username must not be null, ");
		}

		if (StringUtils.isBlank(user.getEmail()))
		{
			sb.append("Email must not be null, ");
		}

		if (user.getTimeofcreation() == null)
		{
			sb.append("Timeofcreation must not be null, ");
		}

		if (user.getStatus() == null)
		{
			sb.append("Status must not be null, ");
		}

		if (StringUtils.isBlank(user.getPassword()))
		{
			sb.append("Password must not be null, ");
		}

		return sb.toString();
	}

	@Override
	public void deleteUser(Long id)
	{
		getUserById(id);
		updateUser(User.builder()
			.id(id)
			.status(UserStatusEnum.INAKTIV)
			.build()
		);
	}

	@Override
	public void deleteAll()
	{
		getAllUsers().forEach(user ->
		{
			user.setStatus(UserStatusEnum.INAKTIV);
			updateUser(user);
		});
	}
}
