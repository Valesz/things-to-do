package org.example.controller;

import io.jsonwebtoken.Claims;
import java.time.LocalDate;
import org.example.AbstractTest;
import org.example.model.User;
import org.example.service.UserService;
import org.example.service.impl.JwtServiceImpl;
import org.example.utils.enums.UserStatusEnum;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest extends AbstractTest
{
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private JwtServiceImpl jwtService;

	private final String baseEndpoint = "/api/auth/";

	private final User user1 = User.builder()
		.username("teszt elek")
		.email("teszt@teszt.teszt")
		.timeofcreation(LocalDate.now())
		.status(UserStatusEnum.AKTIV)
		.password("tesztA12")
		.classification(0.5)
		.precisionofanswers(0.8)
		.build();

	@Before
	public void setUp()
	{
		userService.saveUser(user1);
	}

	@After
	public void tearDown()
	{
		userService.deleteAll();
	}

	@Test
	public void registerUserTest()
	{
		User testUser = User.builder()
			.username("teszt elek2")
			.email("tesz@vesz.teszt")
			.timeofcreation(LocalDate.EPOCH)
			.status(UserStatusEnum.INAKTIV)
			.password("tesztheheA12")
			.classification(0.1)
			.precisionofanswers(0.1)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<User> entity = new HttpEntity<>(testUser, headers);
		ResponseEntity<User> response = restTemplate.postForEntity(baseEndpoint + "register", entity, User.class);

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertNotNull(response.getBody());

		User userResponse = response.getBody();
		Assert.assertEquals(testUser.getUsername(), userResponse.getUsername());
		Assert.assertEquals(testUser.getEmail(), userResponse.getEmail());
		Assert.assertEquals(testUser.getTimeofcreation(), userResponse.getTimeofcreation());
		Assert.assertEquals(testUser.getStatus(), userResponse.getStatus());
		Assert.assertEquals(testUser.getClassification(), userResponse.getClassification());
		Assert.assertEquals(testUser.getPrecisionofanswers(), userResponse.getPrecisionofanswers());
	}

	@Test
	public void loginWithValidCredentials()
	{
		User loginUser = User.builder()
			.username(this.user1.getUsername())
			.password("tesztA12")
			.build();

		ResponseEntity<String> response = restTemplate.postForEntity(baseEndpoint + "login", loginUser, String.class);
		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		String jwtToken = response.getBody();
		Assert.assertNotNull(jwtToken);

		Assert.assertNotNull(jwtService.extractClaim(jwtToken, Claims::getIssuedAt));
		Assert.assertNotNull(jwtService.extractClaim(jwtToken, Claims::getExpiration));
		Assert.assertEquals(this.user1.getUsername(), jwtService.extractUsername(jwtToken));
	}

//	@Test
//	public void loginWithInvalidCredentials() {
//		User loginUser = User.builder()
//			.username(this.user1.getUsername())
//			.password("tesztelek")
//			.build();
//
//		ResponseEntity<HttpErrorResponseForTests> response = restTemplate.postForEntity(baseEndpoint + "login", loginUser, HttpErrorResponseForTests.class);
//
//		Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//		Assert.assertNotNull(response.getBody());
//
//		HttpErrorResponseForTests errorResponse = response.getBody();
//		Assert.assertNotNull(errorResponse);
//		Assert.assertNotNull(errorResponse.getError());
//		Assert.assertEquals("The username or password is incorrect", errorResponse.getMessage());
//	}
}
