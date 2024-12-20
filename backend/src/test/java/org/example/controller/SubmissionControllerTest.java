package org.example.controller;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import org.example.AbstractTest;
import org.example.model.Submission;
import org.example.model.Task;
import org.example.model.User;
import org.example.model.listing.SubmissionListing;
import org.example.model.listing.SubmissionListingResponse;
import org.example.repository.TaskRepository;
import org.example.service.SubmissionService;
import org.example.service.UserService;
import org.example.utils.HttpErrorResponseForTests;
import org.example.utils.enums.SubmissionAcceptanceEnum;
import org.example.utils.enums.UserStatusEnum;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubmissionControllerTest extends AbstractTest
{

	@Autowired
	private SubmissionService submissionService;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private TaskRepository taskRepository;

	private final String baseEndpoint = "/api/submission/";

	private String jwtToken;

	private final User user1 = User.builder()
		.username("Teszt Elek")
		.email("teszt@teszt.teszt")
		.timeofcreation(LocalDate.now())
		.status(UserStatusEnum.AKTIV)
		.password("tesztA12")
		.classification(0.5)
		.precisionofanswers(0.8)
		.build();

	private final User user2 = User.builder()
		.username("Ebéd Elek")
		.email("tesz@vesz.teszt")
		.timeofcreation(LocalDate.EPOCH)
		.status(UserStatusEnum.INAKTIV)
		.password("tesztheheA1")
		.classification(0.1)
		.precisionofanswers(0.1)
		.build();

	private final Task task1 = Task.builder()
		.name("Pelda Task")
		.description("Leiras")
		.timeofcreation(LocalDate.now())
		.ownerid(null)
		.build();

	private final Task task2 = Task.builder()
		.name("Example Task")
		.description("Cool Description")
		.timeofcreation(LocalDate.EPOCH)
		.ownerid(null)
		.build();

	private final Submission submission1 = Submission.builder()
		.taskid(null)
		.description("Good description")
		.timeofsubmission(LocalDate.EPOCH)
		.acceptance(SubmissionAcceptanceEnum.REJECTED)
		.submitterid(null)
		.build();

	private final Submission submission2 = Submission.builder()
		.taskid(null)
		.description("Cool description")
		.timeofsubmission(LocalDate.now())
		.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
		.submitterid(null)
		.build();

	private final Submission submission3 = Submission.builder()
		.taskid(null)
		.description("Good description")
		.timeofsubmission(LocalDate.now())
		.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
		.submitterid(null)
		.build();

	private final Submission submission4 = Submission.builder()
		.taskid(null)
		.description("Cool description")
		.timeofsubmission(LocalDate.EPOCH)
		.acceptance(SubmissionAcceptanceEnum.IN_PROGRESS)
		.submitterid(null)
		.build();

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
	public void setup()
	{
		this.userService.saveUser(user1);
		this.userService.saveUser(user2);

		this.task1.setOwnerid(user1.getId());
		this.task2.setOwnerid(user2.getId());

		this.taskRepository.save(task1);
		this.taskRepository.save(task2);

		this.submission1.setTaskid(this.task1.getId());
		this.submission1.setSubmitterid(this.user1.getId());
		this.submission2.setTaskid(this.task2.getId());
		this.submission2.setSubmitterid(this.user1.getId());
		this.submission3.setTaskid(this.task1.getId());
		this.submission3.setSubmitterid(this.user2.getId());
		this.submission4.setTaskid(this.task2.getId());
		this.submission4.setSubmitterid(this.user2.getId());

		this.submissionService.saveSubmission(submission1);
		this.submissionService.saveSubmission(submission2);
		this.submissionService.saveSubmission(submission3);
		this.submissionService.saveSubmission(submission4);

		if (this.jwtToken == null)
		{
			login();
		}
	}

	@After
	public void teardown()
	{
		this.submissionService.deleteAll();
		this.taskRepository.deleteAll();
		this.userService.deleteAll();
	}

	@Test
	public void addSubmissionTest()
	{
		Submission testSubmission = Submission.builder()
			.taskid(this.task1.getId())
			.description("buena descripción")
			.timeofsubmission(LocalDate.now())
			.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
			.submitterid(this.user1.getId())
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(testSubmission, headers);
		ResponseEntity<Submission> response = this.restTemplate.postForEntity(baseEndpoint, request, Submission.class);
		Submission submissionFromResponse = response.getBody();

		SubmissionListingResponse submissionsFromDb = this.restTemplate.exchange(
			baseEndpoint + "?pagenumber=0&pagesize=100",
			HttpMethod.GET,
			new HttpEntity<>(headers),
			SubmissionListingResponse.class
		).getBody();

		Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		Assert.assertNotNull(submissionFromResponse);
		Assert.assertNotNull(submissionsFromDb);
		Spliterator<SubmissionListing> submissions = submissionsFromDb.getSubmissions().spliterator();

		Assert.assertTrue(StreamSupport.stream(
			submissionsFromDb.getSubmissions().spliterator(),
			false
		).anyMatch(submissionFromResponse::listingObjEquals));

		Assert.assertTrue(
			StreamSupport.stream(submissions, false).anyMatch(
				submission ->
					submission.getTaskid().equals(testSubmission.getTaskid())
					|| submission.getSubmitterid().equals(testSubmission.getSubmitterid())
					|| submission.getDescription().equals(testSubmission.getDescription())
					|| submission.getTimeofsubmission().equals(testSubmission.getTimeofsubmission())
					|| submission.getAcceptance().equals(testSubmission.getAcceptance())
			)
		);
	}

	@Test
	public void addSubmissionWithNullValuesTest()
	{
		Submission submission = Submission.builder()
			.description("descriptión")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(submission, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(
			baseEndpoint,
			request,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addSubmissionWithIdTest()
	{
		Submission testSubmission = Submission.builder()
			.id(1L)
			.taskid(this.task1.getId())
			.description("buena descripción")
			.timeofsubmission(LocalDate.now())
			.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
			.submitterid(this.user1.getId())
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(testSubmission, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(
			baseEndpoint,
			request,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addNullSubmissionTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(null, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(
			baseEndpoint,
			request,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addSubmissionWithInvalidTaskidTest()
	{
		Submission testSubmission = Submission.builder()
			.taskid(Long.MAX_VALUE)
			.description("buena descripción")
			.timeofsubmission(LocalDate.now())
			.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
			.submitterid(this.user1.getId())
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(testSubmission, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(
			baseEndpoint,
			request,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Not Found", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addSubmissionWithoutTaskIdTest()
	{
		Submission testSubmission = Submission.builder()
			.taskid(null)
			.description("buena descripción")
			.timeofsubmission(LocalDate.now())
			.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
			.submitterid(this.user1.getId())
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(testSubmission, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(
			baseEndpoint,
			request,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addSubmissionWithInvalidSubmitteridTest()
	{
		Submission testSubmission = Submission.builder()
			.taskid(this.task1.getId())
			.description("buena descripción")
			.timeofsubmission(LocalDate.now())
			.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
			.submitterid(Long.MAX_VALUE)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(testSubmission, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(
			baseEndpoint,
			request,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Not Found", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addSubmissionWithNullSubmitterIdTest()
	{
		Submission testSubmission = Submission.builder()
			.taskid(null)
			.description("buena descripción")
			.timeofsubmission(LocalDate.now())
			.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
			.submitterid(null)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(testSubmission, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(
			baseEndpoint,
			request,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void getSingleSubmissionTest()
	{
		Submission querySubmission = Submission.builder()
			.id(this.submission1.getId())
			.build();

		String paramsURI = "?id={id}" +
			"&taskid={taskid}" +
			"&description={description}" +
			"&timeofsubmission={timeofsubmission}" +
			"&acceptance={acceptance}" +
			"&submitterid={submitterid}" +
			"&pagenumber=0" +
			"&pagesize=100";

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<SubmissionListingResponse> response = this.restTemplate.exchange(
			baseEndpoint + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			SubmissionListingResponse.class,
			querySubmission.getId(),
			querySubmission.getTaskid(),
			querySubmission.getDescription(),
			querySubmission.getTimeofsubmission(),
			querySubmission.getAcceptance(),
			querySubmission.getSubmitterid()
		);

		SubmissionListingResponse submissionsAccordingToQuery = response.getBody();

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertNotNull(submissionsAccordingToQuery);
		Assert.assertEquals(1, submissionsAccordingToQuery.getSubmissions().spliterator().getExactSizeIfKnown());
		Assert.assertNotNull(submissionsAccordingToQuery.getSubmissions().iterator().next());
	}

	@Test
	public void getMultipleSubmissionsTest()
	{
		Submission querySubmission = Submission.builder()
			.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
			.build();

		String paramsURI = "?acceptance={acceptance}&pagenumber=0&pagesize=100";

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<SubmissionListingResponse> response = this.restTemplate.exchange(
			baseEndpoint + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			SubmissionListingResponse.class,
			querySubmission.getAcceptance()
		);

		SubmissionListingResponse submissionsAccordingToQuery = response.getBody();

		Assert.assertNotNull(submissionsAccordingToQuery);
		Spliterator<SubmissionListing> submissions = submissionsAccordingToQuery.getSubmissions().spliterator();

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertEquals(2, StreamSupport.stream(submissions, false).count());
		Assert.assertTrue(StreamSupport.stream(submissions, false).allMatch(
			submission -> this.submission3.listingObjEquals(submission) || this.submission2.listingObjEquals(submission))
		);
	}

	@Test
	public void getAllSubmissionsTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<SubmissionListingResponse> response = this.restTemplate.exchange(
			baseEndpoint + "?pagenumber=0&pagesize=100",
			HttpMethod.GET,
			new HttpEntity<>(headers),
			SubmissionListingResponse.class
		);

		SubmissionListingResponse submissionsInDb = response.getBody();

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertNotNull(submissionsInDb);
		Spliterator<SubmissionListing> submissions = submissionsInDb.getSubmissions().spliterator();

		Assert.assertEquals(4, StreamSupport.stream(submissions, false).count());
		Assert.assertTrue(StreamSupport.stream(submissions, false).allMatch(
			submission -> this.submission1.listingObjEquals(submission)
				|| this.submission2.listingObjEquals(submission)
				|| this.submission3.listingObjEquals(submission)
				|| this.submission4.listingObjEquals(submission)
		));
	}

	@Test
	public void updateSubmissionTest()
	{
		Submission propertiesToUpdate = Submission.builder()
			.id(this.submission1.getId())
			.taskid(this.task2.getId())
			.description("buena descripción")
			.timeofsubmission(LocalDate.now())
			.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
			.submitterid(this.user2.getId())
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<Submission> response = this.restTemplate.exchange(
			baseEndpoint,
			HttpMethod.PUT,
			request,
			Submission.class
		);
		Submission submissionFromResponse = response.getBody();

		SubmissionListingResponse submissionsFromDb = this.restTemplate.exchange(
			baseEndpoint + "?pagenumber=0&pagesize=100",
			HttpMethod.GET,
			new HttpEntity<>(headers),
			SubmissionListingResponse.class
		).getBody();

		Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
		Assert.assertNotNull(submissionFromResponse);
		Assert.assertNotNull(submissionsFromDb);
		Spliterator<SubmissionListing> submissions = submissionsFromDb.getSubmissions().spliterator();

		Assert.assertTrue(StreamSupport.stream(submissions, false).anyMatch(
			submissionFromResponse::listingObjEquals
		));
		Assert.assertEquals(propertiesToUpdate.getTaskid(), submissionFromResponse.getTaskid());
		Assert.assertEquals(propertiesToUpdate.getDescription(), submissionFromResponse.getDescription());
		Assert.assertEquals(propertiesToUpdate.getTimeofsubmission(), submissionFromResponse.getTimeofsubmission());
		Assert.assertEquals(propertiesToUpdate.getAcceptance(), submissionFromResponse.getAcceptance());
		Assert.assertEquals(propertiesToUpdate.getSubmitterid(), submissionFromResponse.getSubmitterid());
	}

	@Test
	public void updateSubmissionWithoutIdTest()
	{
		Submission propertiesToUpdate = Submission.builder()
			.description("descriptión")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.exchange(
			baseEndpoint,
			HttpMethod.PUT,
			request,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void updateTaskWithInvalidIdTest()
	{
		Submission propertiesToUpdate = Submission.builder()
			.id(Long.MAX_VALUE)
			.description("descriptión")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.exchange(
			baseEndpoint,
			HttpMethod.PUT,
			request,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Not Found", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void updateSubmissionToInvalidTaskIdTest()
	{
		Submission propertiesToUpdate = Submission.builder()
			.id(this.submission1.getId())
			.taskid(Long.MAX_VALUE)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.exchange(
			baseEndpoint,
			HttpMethod.PUT,
			request,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Not Found", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void updateSubmissionToInvalidSubmitterIdTest()
	{
		Submission propertiesToUpdate = Submission.builder()
			.id(this.submission1.getId())
			.submitterid(Long.MAX_VALUE)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Submission> request = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.exchange(
			baseEndpoint,
			HttpMethod.PUT,
			request,
			HttpErrorResponseForTests.class
		);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Not Found", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void deleteSubmissionTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<Void> response = this.restTemplate.exchange(
			baseEndpoint + this.submission1.getId(),
			HttpMethod.DELETE,
			new HttpEntity<>(headers),
			Void.class
		);

		ResponseEntity<SubmissionListingResponse> submissionsFromDb = this.restTemplate.exchange(
			baseEndpoint + "?pagenumber=0&pagesize=100",
			HttpMethod.GET,
			new HttpEntity<>(headers),
			SubmissionListingResponse.class
		);
		SubmissionListingResponse submissionsAccordingToQuery = submissionsFromDb.getBody();

		Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		Assert.assertNotNull(submissionsAccordingToQuery);
		Spliterator<SubmissionListing> submissions = submissionsAccordingToQuery.getSubmissions().spliterator();

		Assert.assertEquals(3, StreamSupport.stream(submissions, false).count());
		Assert.assertTrue(StreamSupport.stream(submissions, false).allMatch(
			submission -> this.submission2.listingObjEquals(submission)
				|| this.submission3.listingObjEquals(submission)
				|| this.submission4.listingObjEquals(submission)
		));
	}

	@Test
	public void deleteSubmissionWithNonExistingIdTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.exchange(
			baseEndpoint + "-1",
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
