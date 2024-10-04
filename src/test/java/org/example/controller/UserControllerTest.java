package org.example.controller;

import org.example.AbstractTest;
import org.example.model.User;
import org.example.service.UserService;
import org.example.utils.HttpErrorResponseForTests;
import org.example.utils.UserStatusEnum;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest extends AbstractTest
{

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserService userService;

	private final User user1 = User.builder()
		.username("teszt elek")
		.email("teszt@teszt.teszt")
		.timeofcreation(LocalDate.now())
		.status(UserStatusEnum.AKTIV)
		.password("teszt")
		.classification(0.5)
		.precisionofanswers(0.8)
		.build();

	private final User user2 = User.builder()
		.username("teszt elek")
		.email("tesz@vesz.teszt")
		.timeofcreation(LocalDate.EPOCH)
		.status(UserStatusEnum.INAKTIV)
		.password("teszthehe")
		.classification(0.1)
		.precisionofanswers(0.1)
		.build();

	private final User user3 = User.builder()
		.username("Cserepes Virág")
		.email("teszt@tesztel.tesztelek")
		.timeofcreation(LocalDate.now())
		.status(UserStatusEnum.AKTIV)
		.password("teszt")
		.classification(0.1)
		.precisionofanswers(0.8)
		.build();

	private final User user4 = User.builder()
		.username("Cserepes Kamilla")
		.email("tesz@vesz.teszt")
		.timeofcreation(LocalDate.now())
		.status(UserStatusEnum.AKTIV)
		.password("teszthehe")
		.classification(0.5)
		.precisionofanswers(0.1)
		.build();

	String baseURI = "/api/user/";

	@Before
	public void setUp()
	{
		userService.saveUser(user1);
		userService.saveUser(user2);
		userService.saveUser(user3);
		userService.saveUser(user4);
	}

	@After
	public void teardown()
	{
		userService.deleteAll();
	}

	@Test
	public void addUserTest()
	{
		User testUser = User.builder()
			.username("Ebéd Elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		HttpHeaders headersPost = new HttpHeaders();
		headersPost.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headersPost.add(HttpHeaders.ACCEPT, "application/json");

		HttpEntity<User> requestBodyWithHeaders = new HttpEntity<>(testUser, headersPost);
		ResponseEntity<User> responseEntity = this.restTemplate.postForEntity("/api/user/", requestBodyWithHeaders, User.class);

		User[] usersInDb = this.restTemplate.getForEntity("/api/user/", User[].class).getBody();

		Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		Assert.assertNotNull(usersInDb);
		Assert.assertEquals(5, usersInDb.length);
		Assert.assertEquals(user1, usersInDb[0]);
		Assert.assertEquals(user2, usersInDb[1]);
		Assert.assertEquals(user3, usersInDb[2]);
		Assert.assertEquals(user4, usersInDb[3]);
		Assert.assertEquals(testUser.getUsername(), usersInDb[4].getUsername());
		Assert.assertEquals(testUser.getEmail(), usersInDb[4].getEmail());
		Assert.assertEquals(testUser.getTimeofcreation(), usersInDb[4].getTimeofcreation());
		Assert.assertEquals(testUser.getStatus(), usersInDb[4].getStatus());
		Assert.assertEquals(testUser.getClassification(), usersInDb[4].getClassification());
		Assert.assertEquals(testUser.getPrecisionofanswers(), usersInDb[4].getPrecisionofanswers());
	}

	@Test
	public void addUserWithNullValuesTest()
	{
		User testUser = User.builder()
			.username("Ebéd Elek")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add(HttpHeaders.ACCEPT, "application/json");

		HttpEntity<User> requestBodyWithHeaders = new HttpEntity<>(testUser, headers);
		ResponseEntity<HttpErrorResponseForTests> responseEntity = this.restTemplate.postForEntity("/api/user/", requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assert.assertNotNull(responseEntity.getBody());
		Assert.assertEquals("Bad Request", responseEntity.getBody().getError());
		Assert.assertNotNull(responseEntity.getBody().getMessage());
	}

	@Test
	public void addUserWithIdTest()
	{
		User testUser = User.builder()
			.id(1L)
			.username("Ebéd Elek")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add(HttpHeaders.ACCEPT, "application/json");

		HttpEntity<User> requestBodyWithHeaders = new HttpEntity<>(testUser, headers);
		ResponseEntity<HttpErrorResponseForTests> responseEntity = this.restTemplate.postForEntity("/api/user/", requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
		Assert.assertNotNull(responseEntity.getBody());
		Assert.assertEquals("Unprocessable Entity", responseEntity.getBody().getError());
		Assert.assertNotNull(responseEntity.getBody().getMessage());
	}

	@Test
	public void addNullUserTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add(HttpHeaders.ACCEPT, "application/json");

		HttpEntity<Object> requestBodyWithHeaders = new HttpEntity<>(null, headers);
		ResponseEntity<HttpErrorResponseForTests> responseEntity = this.restTemplate.postForEntity("/api/user/", requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assert.assertNotNull(responseEntity.getBody());
		Assert.assertEquals("Bad Request", responseEntity.getBody().getError());
		Assert.assertNotNull(responseEntity.getBody().getMessage());
	}

	@Test
	public void getSingleUserTest()
	{
		User queryUser = User.builder()
			.id(this.user1.getId())
			.username("teszt elek")
			.email("teszt@teszt.teszt")
			.timeofcreation(LocalDate.now())
			.status(UserStatusEnum.AKTIV)
			.password("teszt")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		HttpHeaders headersPost = new HttpHeaders();

		String paramsURI = "?id={id}" +
			"&username={username}" +
			"&email={email}" +
			"&timeofcreation={timeofcreation}" +
			"&status={status}" +
			"&classification={classification}" +
			"&precisionofanswers={precisionofanswers}";

		ResponseEntity<User[]> responseEntity = restTemplate.exchange(baseURI + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headersPost),
			User[].class,
			queryUser.getId(),
			queryUser.getUsername(),
			queryUser.getEmail(),
			queryUser.getTimeofcreation(),
			queryUser.getStatus(),
			queryUser.getClassification(),
			queryUser.getPrecisionofanswers()
		);

		User[] usersAccordingToQuery = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(usersAccordingToQuery);
		Assert.assertEquals(1, usersAccordingToQuery.length);
		Assert.assertEquals(user1, usersAccordingToQuery[0]);
	}

	@Test
	public void getMultipleUsersTest()
	{
		User queryUser = User.builder()
			.username("teszt elek")
			.build();

		HttpHeaders headersPost = new HttpHeaders();
		headersPost.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headersPost.add(HttpHeaders.ACCEPT, "application/json");

		String paramsURI = "?id={id}" +
			"&username={username}" +
			"&email={email}" +
			"&timeofcreation={timeofcreation}" +
			"&status={status}" +
			"&classification={classification}" +
			"&precisionofanswers={precisionofanswers}";

		ResponseEntity<User[]> responseEntity = restTemplate.exchange(baseURI + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headersPost),
			User[].class,
			queryUser.getId(),
			queryUser.getUsername(),
			queryUser.getEmail(),
			queryUser.getTimeofcreation(),
			queryUser.getStatus(),
			queryUser.getClassification(),
			queryUser.getPrecisionofanswers()
		);

		User[] usersAccordingToQuery = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(usersAccordingToQuery);
		Assert.assertEquals(2, usersAccordingToQuery.length);
		Assert.assertEquals(user1, usersAccordingToQuery[0]);
		Assert.assertEquals(user2, usersAccordingToQuery[1]);
	}

	@Test
	public void getAllUsersTest()
	{
		ResponseEntity<User[]> response = this.restTemplate.getForEntity("/api/user/", User[].class);
		User[] usersArray = response.getBody();

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertNotNull(usersArray);
		Assert.assertEquals(4, usersArray.length);
		Assert.assertEquals(user1, usersArray[0]);
		Assert.assertEquals(user2, usersArray[1]);
		Assert.assertEquals(user3, usersArray[2]);
		Assert.assertEquals(user4, usersArray[3]);
	}

	@Test
	public void updateUserTest()
	{
		User propertiesToUpgrade = User.builder()
			.id(user1.getId())
			.username("Csin Csilla")
			.email("csinos@csilla.hu")
			.timeofcreation(LocalDate.EPOCH)
			.status(UserStatusEnum.INAKTIV)
			.password("Csincsilla")
			.classification(1.0)
			.precisionofanswers(1.0)
			.build();

		ResponseEntity<User> responseFromUpdate = this.restTemplate.exchange(baseURI, HttpMethod.PUT, new HttpEntity<>(propertiesToUpgrade), User.class);
		Assert.assertEquals(HttpStatus.OK, responseFromUpdate.getStatusCode());

		ResponseEntity<User[]> responseFromDb = this.restTemplate.getForEntity("/api/user/?id={id}", User[].class, propertiesToUpgrade.getId());
		Assert.assertNotNull(responseFromDb.getBody());
		User userFromDb = responseFromDb.getBody()[0];

		Assert.assertEquals(propertiesToUpgrade.getUsername(), userFromDb.getUsername());
		Assert.assertEquals(propertiesToUpgrade.getEmail(), userFromDb.getEmail());
		Assert.assertEquals(propertiesToUpgrade.getTimeofcreation(), userFromDb.getTimeofcreation());
		Assert.assertEquals(propertiesToUpgrade.getStatus(), userFromDb.getStatus());
		Assert.assertEquals(propertiesToUpgrade.getClassification(), userFromDb.getClassification());
		Assert.assertEquals(propertiesToUpgrade.getPrecisionofanswers(), userFromDb.getPrecisionofanswers());
	}

	@Test
	public void updateUserWithoutIdTest()
	{
		User propertiesToUpdate = User.builder()
			.username("Sanyi a ló")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add(HttpHeaders.ACCEPT, "application/json");

		HttpEntity<User> requestBodyWithHeaders = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> responseEntity = this.restTemplate.exchange("/api/user/", HttpMethod.PUT, requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assert.assertNotNull(responseEntity.getBody());
		Assert.assertEquals("Bad Request", responseEntity.getBody().getError());
		Assert.assertNotNull(responseEntity.getBody().getMessage());
	}

	@Test
	public void updateUserWithInvalidIdTest()
	{
		User propertiesToUpdate = User.builder()
			.id(Long.MAX_VALUE)
			.username("Sanyi a ló")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add(HttpHeaders.ACCEPT, "application/json");

		HttpEntity<User> requestBodyWithHeaders = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> responseEntity = this.restTemplate.exchange("/api/user/", HttpMethod.PUT, requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
		Assert.assertNotNull(responseEntity.getBody());
		Assert.assertEquals("Not Found", responseEntity.getBody().getError());
		Assert.assertNotNull(responseEntity.getBody().getMessage());
	}

	@Test
	public void deleteUserTest()
	{
		this.restTemplate.exchange(baseURI + user1.getId(), HttpMethod.DELETE, new HttpEntity<>(user1), Void.class);

		ResponseEntity<User[]> response = this.restTemplate.getForEntity("/api/user/", User[].class);
		Assert.assertNotNull(response.getBody());

		User[] usersArray = Arrays.stream(response.getBody())
			.filter(value -> value.getStatus() == UserStatusEnum.AKTIV)
			.toArray(User[]::new);
		Assert.assertNotNull(usersArray);

		Assert.assertEquals(2, usersArray.length);
	}

	@Test
	public void deleteUserWithNonExistingIdTest()
	{
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.exchange(baseURI + "-1", HttpMethod.DELETE, new HttpEntity<>(null), HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Not Found", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}
}
