package org.example.service;

import org.example.AbstractTest;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.utils.enums.UserStatusEnum;
import org.example.utils.exceptions.ServiceException;
import org.example.utils.exceptions.ServiceExceptionType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

public class UserServiceImplTest extends AbstractTest
{

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@After
	public void teardown()
	{
		userRepository.deleteAll();
	}

	@Test
	public void saveValidUserTest()
	{
		User user = User.builder()
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		userService.saveUser(user);

		User savedUser = userService.getByUsersObject(User.builder()
			.id(user.getId())
			.build()
		).iterator().next();

		Assert.assertEquals(user, savedUser);
	}

	@Test
	public void saveInvalidUserTest()
	{
		User user1 = User.builder()
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.build();

		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> userService.saveUser(user1));
		Assert.assertEquals(ServiceExceptionType.NULL_ARGUMENT, exception.getServiceExceptionTypeEnum());

		User user2 = User.builder()
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		exception = Assert.assertThrows(ServiceException.class, () -> userService.saveUser(user2));
		Assert.assertEquals(ServiceExceptionType.NULL_ARGUMENT, exception.getServiceExceptionTypeEnum());

		User user3 = User.builder()
			.id(1L)
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();
		exception = Assert.assertThrows(ServiceException.class, () -> userService.saveUser(user3));
		Assert.assertEquals(ServiceExceptionType.ID_GIVEN, exception.getServiceExceptionTypeEnum());
	}

	@Test
	public void getUserByIdTest()
	{
		User user = User.builder()
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		userService.saveUser(user);

		Assert.assertEquals(user, userService.getUserById(user.getId()));
	}

	@Test
	public void getUserByObjectTest()
	{
		User user1 = User.builder()
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		User user2 = User.builder()
			.username("teszt elek2")
			.email("tesz@vesz.teszt")
			.timeofcreation(LocalDate.EPOCH)
			.status(UserStatusEnum.INAKTIV)
			.password("teszthehe")
			.classification(0.1)
			.precisionofanswers(0.1)
			.build();

		User user3 = User.builder()
			.username("Cserepes Virág")
			.email("teszt@tesztel.tesztelek")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.1)
			.precisionofanswers(0.8)
			.build();

		User user4 = User.builder()
			.username("Cserepes Kamilla")
			.email("tesz@vesz.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszthehe")
			.classification(0.5)
			.precisionofanswers(0.1)
			.build();

		userService.saveUser(user1);
		userService.saveUser(user2);
		userService.saveUser(user3);
		userService.saveUser(user4);

		Iterable<User> userIterable = userService.getByUsersObject(User.builder()
			.id(user1.getId())
			.build()
		);
		Assert.assertTrue(StreamSupport.stream(userIterable.spliterator(), false).allMatch(user1::equals));

		userIterable = userService.getByUsersObject(User.builder()
			.username("teszt elek")
			.build()
		);
		Assert.assertTrue(StreamSupport.stream(userIterable.spliterator(), false).allMatch(user -> user1.equals(user) || user2.equals(user)));

		userIterable = userService.getByUsersObject(User.builder()
			.email("tesz@vesz.teszt")
			.build()
		);
		Assert.assertTrue(StreamSupport.stream(userIterable.spliterator(), false).allMatch(user -> user2.equals(user) || user4.equals(user)));

		userIterable = userService.getByUsersObject(User.builder()
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.build()
		);
		Assert.assertTrue(StreamSupport.stream(userIterable.spliterator(), false).allMatch(user -> user1.equals(user) || user3.equals(user) || user4.equals(user)));

		userIterable = userService.getByUsersObject(User.builder()
			.precisionofanswers(0.8)
			.build()
		);
		Assert.assertTrue(StreamSupport.stream(userIterable.spliterator(), false).allMatch(user -> user1.equals(user) || user3.equals(user)));

		userIterable = userService.getByUsersObject(User.builder()
			.classification(0.5)
			.build()
		);
		Assert.assertTrue(StreamSupport.stream(userIterable.spliterator(), false).allMatch(user -> user1.equals(user) || user4.equals(user)));

		userIterable = userService.getByUsersObject(null);
		Assert.assertEquals(4, StreamSupport.stream(userIterable.spliterator(), false).count());
	}

	@Test
	public void getAllUsersTest()
	{
		User user = User.builder()
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		userService.saveUser(user);
		Assert.assertEquals(user, userService.getUserById(user.getId()));

		user.setId(null);
		user.setUsername(user.getUsername() + "2");
		userService.saveUser(user);
		Assert.assertEquals(user, userService.getUserById(user.getId()));

		List<User> users = StreamSupport.stream(userService.getAllUsers().spliterator(), false).toList();

		Assert.assertEquals(2, users.size());
	}

	@Test
	public void updateUserWithSingleValueChangesTest()
	{
		User user = User.builder()
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		userService.saveUser(user);
		Assert.assertEquals(user, userService.getUserById(user.getId()));

		User updateUserProperties = User.builder()
			.id(user.getId())
			.username("Cicam Ica")
			.build();

		userService.updateUser(updateUserProperties);
		Assert.assertEquals(updateUserProperties.getUsername(), userService.getUserById(user.getId()).getUsername());

		updateUserProperties.setUsername(null);
		updateUserProperties.setEmail("savanyu@citromail.hu");

		userService.updateUser(updateUserProperties);
		Assert.assertEquals(updateUserProperties.getEmail(), userService.getUserById(user.getId()).getEmail());

		updateUserProperties.setEmail(null);
		updateUserProperties.setTimeofcreation(LocalDate.now());

		userService.updateUser(updateUserProperties);
		Assert.assertEquals(updateUserProperties.getTimeofcreation(), userService.getUserById(user.getId()).getTimeofcreation());

		updateUserProperties.setTimeofcreation(null);
		updateUserProperties.setStatus(UserStatusEnum.INAKTIV);

		userService.updateUser(updateUserProperties);
		Assert.assertEquals(updateUserProperties.getStatus(), userService.getUserById(user.getId()).getStatus());

		updateUserProperties.setStatus(null);
		updateUserProperties.setClassification(0.9);

		userService.updateUser(updateUserProperties);
		Assert.assertEquals(updateUserProperties.getClassification(), userService.getUserById(user.getId()).getClassification());

		updateUserProperties.setClassification(null);
		updateUserProperties.setPrecisionofanswers(0.1);

		userService.updateUser(updateUserProperties);
		Assert.assertEquals(updateUserProperties.getPrecisionofanswers(), userService.getUserById(user.getId()).getPrecisionofanswers());
	}

	@Test
	public void updateUserWithoutIdTest()
	{
		User user = User.builder()
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> userService.updateUser(user));
		Assert.assertEquals(exception.getServiceExceptionTypeEnum(), ServiceExceptionType.ID_NOT_GIVEN);
	}

	@Test
	public void updateUserWithoutValidIdTest()
	{
		User user = User.builder()
			.id(Long.MAX_VALUE)
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> userService.updateUser(user));
		Assert.assertEquals(exception.getServiceExceptionTypeEnum(), ServiceExceptionType.ID_NOT_FOUND);
	}

	@Test
	public void deleteUserTest()
	{
		User user = User.builder()
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		userService.saveUser(user);

		User userInDb = userService.getUserById(user.getId());

		Assert.assertNotNull(userInDb);
		Assert.assertEquals(userInDb.getStatus(), user.getStatus());

		userService.deleteUser(user.getId());

		userInDb = userService.getUserById(user.getId());
		Assert.assertNotNull(userInDb);
		Assert.assertEquals(userInDb.getStatus(), UserStatusEnum.INAKTIV);
	}

	@Test
	public void deleteAllUserTest()
	{
		User user = User.builder()
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		userService.saveUser(user);
		Assert.assertNotNull(userService.getUserById(user.getId()));

		long oldUserId = user.getId();

		user.setId(null);
		user.setUsername(user.getUsername() + "2");
		userService.saveUser(user);
		Assert.assertNotNull(userService.getUserById(user.getId()));

		userService.deleteAll();

		Assert.assertNotNull(userService.getUserById(oldUserId));
		Assert.assertNotNull(userService.getUserById(user.getId()));

		Assert.assertEquals(UserStatusEnum.INAKTIV, userService.getUserById(oldUserId).getStatus());
		Assert.assertEquals(UserStatusEnum.INAKTIV, userService.getUserById(user.getId()).getStatus());
	}
}
