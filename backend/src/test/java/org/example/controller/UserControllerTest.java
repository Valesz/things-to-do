package org.example.controller;

import java.util.Spliterator;
import java.util.stream.StreamSupport;
import org.example.AbstractTest;
import org.example.model.User;
import org.example.model.listing.UserListingResponse;
import org.example.service.UserService;
import org.example.utils.HttpErrorResponseForTests;
import org.example.utils.enums.UserStatusEnum;
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

	private String jwtToken;

	private final User user1 = User.builder()
		.username("teszt elek")
		.email("teszt@teszt.teszt")
		.timeofcreation(LocalDate.now())
		.status(UserStatusEnum.AKTIV)
		.password("tesztA12")
		.classification(0.5)
		.precisionofanswers(0.8)
		.build();

	private final User user2 = User.builder()
		.username("teszt elek2")
		.email("tesz@vesz.teszt")
		.timeofcreation(LocalDate.EPOCH)
		.status(UserStatusEnum.INAKTIV)
		.password("tesztheheA12")
		.classification(0.1)
		.precisionofanswers(0.1)
		.build();

	private final User user3 = User.builder()
		.username("Cserepes Virág")
		.email("teszt@tesztel.tesztelek")
		.timeofcreation(LocalDate.now())
		.status(UserStatusEnum.AKTIV)
		.password("tesztA12")
		.classification(0.1)
		.precisionofanswers(0.8)
		.build();

	private final User user4 = User.builder()
		.username("Cserepes Kamilla")
		.email("tesz@vesz.teszt")
		.timeofcreation(LocalDate.now())
		.status(UserStatusEnum.AKTIV)
		.password("tesztheheA12")
		.classification(0.5)
		.precisionofanswers(0.1)
		.build();

	final String baseURI = "/api/user/";

	private void login()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		User loginUser = User.builder()
			.username(this.user1.getUsername())
			.password("tesztA12")
			.build();
		HttpEntity<User> entity = new HttpEntity<>(loginUser, headers);

		ResponseEntity<String> response = this.restTemplate.postForEntity("/api/auth/login", entity, String.class);
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		this.jwtToken = response.getBody();
		Assert.assertNotNull(this.jwtToken);
	}

	@Before
	public void setUp()
	{
		userService.saveUser(user1);
		userService.saveUser(user2);
		userService.saveUser(user3);
		userService.saveUser(user4);

		if (this.jwtToken == null)
		{
			login();
		}
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
			.password("tesztA12")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		HttpHeaders headersPost = new HttpHeaders();
		headersPost.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headersPost.add(HttpHeaders.ACCEPT, "application/json");
		headersPost.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<User> requestBodyWithHeaders = new HttpEntity<>(testUser, headersPost);
		ResponseEntity<User> responseEntity = this.restTemplate.postForEntity(baseURI, requestBodyWithHeaders, User.class);

		HttpEntity<Void> headersForGet = new HttpEntity<>(headersPost);
		UserListingResponse usersInDb = this.restTemplate.exchange(
			baseURI + "?pagenumber=0&pagesize=100",
			HttpMethod.GET,
			headersForGet,
			UserListingResponse.class
		).getBody();

		Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		Assert.assertNotNull(usersInDb);
		Spliterator<User> users = usersInDb.getUsers().spliterator();

		Assert.assertEquals(5, StreamSupport.stream(users, false).count());
		Assert.assertTrue(StreamSupport.stream(usersInDb.getUsers().spliterator(), false).anyMatch(
			user -> user.getUsername().equals(testUser.getUsername())
				|| user.getEmail().equals(testUser.getEmail())
				|| user.getTimeofcreation().equals(testUser.getTimeofcreation())
				|| user.getStatus().equals(testUser.getStatus())
				|| user.getClassification().equals(testUser.getClassification())
				|| user.getPrecisionofanswers().equals(testUser.getPrecisionofanswers())
		));
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
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

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
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<User> requestBodyWithHeaders = new HttpEntity<>(testUser, headers);
		ResponseEntity<HttpErrorResponseForTests> responseEntity = this.restTemplate.postForEntity("/api/user/", requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assert.assertNotNull(responseEntity.getBody());
		Assert.assertEquals("Bad Request", responseEntity.getBody().getError());
		Assert.assertNotNull(responseEntity.getBody().getMessage());
	}

	@Test
	public void addNullUserTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add(HttpHeaders.ACCEPT, "application/json");
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

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
			.password("tesztA12")
			.classification(0.5)
			.precisionofanswers(0.8)
			.build();

		HttpHeaders headersPost = new HttpHeaders();
		headersPost.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		String paramsURI = "?id={id}" +
			"&username={username}" +
			"&email={email}" +
			"&status={status}" +
			"&classification={classification}" +
			"&precisionofanswers={precisionofanswers}" +
			"&pagenumber=0" +
			"&pagesize=100";

		ResponseEntity<UserListingResponse> responseEntity = restTemplate.exchange(
			baseURI + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headersPost),
			UserListingResponse.class,
			queryUser.getId(),
			queryUser.getUsername(),
			queryUser.getEmail(),
			queryUser.getStatus(),
			queryUser.getClassification(),
			queryUser.getPrecisionofanswers()
		);

		UserListingResponse usersAccordingToQuery = responseEntity.getBody();
		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(usersAccordingToQuery);
		Assert.assertEquals(1, usersAccordingToQuery.getUsers().spliterator().getExactSizeIfKnown());
		Assert.assertEquals(user1, usersAccordingToQuery.getUsers().iterator().next());
	}

	@Test
	public void getMultipleUsersTest()
	{
		User queryUser = User.builder()
			.email("tesz@vesz.teszt")
			.build();

		HttpHeaders headersPost = new HttpHeaders();
		headersPost.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headersPost.add(HttpHeaders.ACCEPT, "application/json");
		headersPost.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		String paramsURI = "?id={id}" +
			"&username={username}" +
			"&email={email}" +
			"&status={status}" +
			"&classification={classification}" +
			"&precisionofanswers={precisionofanswers}" +
			"&pagenumber=0" +
			"&pagesize=100";

		ResponseEntity<UserListingResponse> responseEntity = restTemplate.exchange(
			baseURI + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headersPost),
			UserListingResponse.class,
			queryUser.getId(),
			queryUser.getUsername(),
			queryUser.getEmail(),
			queryUser.getStatus(),
			queryUser.getClassification(),
			queryUser.getPrecisionofanswers()
		);

		UserListingResponse usersAccordingToQuery = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(usersAccordingToQuery);
		Spliterator<User> users = usersAccordingToQuery.getUsers().spliterator();

		Assert.assertEquals(2, users.getExactSizeIfKnown());
		Assert.assertTrue(
			StreamSupport.stream(users, false).allMatch(
				user -> this.user2.equals(user) || this.user4.equals(user)
			)
		);
	}

	@Test
	public void getAllUsersTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);
		ResponseEntity<UserListingResponse> response = this.restTemplate.exchange(
			baseURI + "?pagenumber=0&pagesize=100",
			HttpMethod.GET,
			new HttpEntity<>(headers),
			UserListingResponse.class
		);
		UserListingResponse usersArray = response.getBody();

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertNotNull(usersArray);
		Spliterator<User> users = usersArray.getUsers().spliterator();

		Assert.assertEquals(4, users.getExactSizeIfKnown());
		Assert.assertTrue(StreamSupport.stream(users, false).allMatch(
			user -> this.user1.equals(user)
				|| this.user2.equals(user)
				|| this.user3.equals(user)
				|| this.user4.equals(user)
		));
	}

	@Test
	public void updateUserTest()
	{
		User propertiesToUpdate = User.builder()
			.id(user1.getId())
			.username("Csin Csilla")
			.email("csinos@csilla.hu")
			.timeofcreation(LocalDate.EPOCH)
			.status(UserStatusEnum.INAKTIV)
			.password("Csincsilla")
			.classification(1.0)
			.precisionofanswers(1.0)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<User> responseFromUpdate = this.restTemplate.exchange(
			baseURI,
			HttpMethod.PUT,
			new HttpEntity<>(propertiesToUpdate, headers),
			User.class
		);
		Assert.assertEquals(HttpStatus.OK, responseFromUpdate.getStatusCode());

		ResponseEntity<UserListingResponse> responseFromDb = this.restTemplate.exchange(
			baseURI + "?id={id}&pagenumber=0&pagesize=100",
			HttpMethod.GET,
			new HttpEntity<>(null),
			UserListingResponse.class,
			propertiesToUpdate.getId()
		);
		Assert.assertNotNull(responseFromDb.getBody());
		UserListingResponse usersFromDb = responseFromDb.getBody();
		User userFromDb = StreamSupport.stream(usersFromDb.getUsers().spliterator(), false).filter(
			user -> user.getId().equals(responseFromUpdate.getBody().getId())
		).findFirst().orElse(new User());

		Assert.assertEquals(propertiesToUpdate.getUsername(), userFromDb.getUsername());
		Assert.assertEquals(propertiesToUpdate.getEmail(), userFromDb.getEmail());
		Assert.assertEquals(propertiesToUpdate.getTimeofcreation(), userFromDb.getTimeofcreation());
		Assert.assertEquals(propertiesToUpdate.getStatus(), userFromDb.getStatus());
		Assert.assertEquals(propertiesToUpdate.getClassification(), userFromDb.getClassification());
		Assert.assertEquals(propertiesToUpdate.getPrecisionofanswers(), userFromDb.getPrecisionofanswers());
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
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<User> requestBodyWithHeaders = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> responseEntity = this.restTemplate.exchange(
			baseURI,
			HttpMethod.PUT,
			requestBodyWithHeaders,
			HttpErrorResponseForTests.class
		);

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
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<User> requestBodyWithHeaders = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> responseEntity = this.restTemplate.exchange(
			baseURI,
			HttpMethod.PUT,
			requestBodyWithHeaders,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
		Assert.assertNotNull(responseEntity.getBody());
		Assert.assertEquals("Not Found", responseEntity.getBody().getError());
		Assert.assertNotNull(responseEntity.getBody().getMessage());
	}

	@Test
	public void updateUserWithNullIdTest()
	{
		User propertiesToUpdate = User.builder()
			.id(null)
			.username("Sanyi a ló")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add(HttpHeaders.ACCEPT, "application/json");
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<User> requestBodyWithHeaders = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> responseEntity = this.restTemplate.exchange(
			baseURI,
			HttpMethod.PUT,
			requestBodyWithHeaders,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		Assert.assertNotNull(responseEntity.getBody());
		Assert.assertNotNull(responseEntity.getBody().getMessage());
	}

	@Test
	public void deleteUserTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		this.restTemplate.exchange(
			baseURI + user1.getId(),
			HttpMethod.DELETE,
			new HttpEntity<>(user1, headers),
			Void.class
		);

		ResponseEntity<UserListingResponse> response = this.restTemplate.exchange(
			baseURI + "?pagenumber=0&pagesize=100",
			HttpMethod.GET,
			new HttpEntity<>(headers),
			UserListingResponse.class
		);
		Assert.assertNotNull(response.getBody());

		User[] usersArray = StreamSupport.stream(response.getBody().getUsers().spliterator(), false)
			.filter(value -> value.getStatus() == UserStatusEnum.AKTIV)
			.toArray(User[]::new);
		Assert.assertNotNull(usersArray);

		Assert.assertEquals(2, usersArray.length);
	}

	@Test
	public void deleteUserWithNonExistingIdTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.exchange(
			baseURI + "-1",
			HttpMethod.DELETE,
			new HttpEntity<>(headers),
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Not Found", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}
}
