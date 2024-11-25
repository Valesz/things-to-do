package org.example.controller;

import org.example.AbstractTest;
import org.example.model.KeywordsForTasks;
import org.example.model.Task;
import org.example.model.User;
import org.example.repository.KeywordsForTasksRepository;
import org.example.model.listing.TaskListingFilter;
import org.example.service.TaskService;
import org.example.service.UserService;
import org.example.utils.HttpErrorResponseForTests;
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
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest extends AbstractTest
{

	@Autowired
	private TaskService taskService;

	@Autowired
	private UserService userService;

	@Autowired
	private KeywordsForTasksRepository keywordsForTasksRepository;

	@Autowired
	private TestRestTemplate restTemplate;

	private String jwtToken;

	private final String baseEndpoint = "/api/task/";

	private final String keywordsEndpoint = baseEndpoint + "keyword/";

	private final User user1 = User.builder()
		.username("Teszt Elek")
		.email("teszt@teszt.teszt")
		.timeofcreation(LocalDate.now())
		.status(UserStatusEnum.AKTIV)
		.password("teszt")
		.classification(0.5)
		.precisionofanswers(0.8)
		.build();

	private final User user2 = User.builder()
		.username("Ebéd Elek")
		.email("tesz@vesz.teszt")
		.timeofcreation(LocalDate.EPOCH)
		.status(UserStatusEnum.INAKTIV)
		.password("teszthehe")
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

	private final Task task3 = Task.builder()
		.name("Pelda Task")
		.description("Leiras")
		.timeofcreation(LocalDate.EPOCH)
		.ownerid(null)
		.maintaskid(task1.getId())
		.build();

	private final Task task4 = Task.builder()
		.name("Example Task")
		.description("Cool Description")
		.timeofcreation(LocalDate.now())
		.ownerid(null)
		.maintaskid(task2.getId())
		.build();

	private final KeywordsForTasks keyword1 = KeywordsForTasks.builder().taskid(null).keyword("ABC").build();
	private final KeywordsForTasks keyword2 = KeywordsForTasks.builder().taskid(null).keyword("DEF").build();
	private final KeywordsForTasks keyword3 = KeywordsForTasks.builder().taskid(null).keyword("ABC").build();

	private void login()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		User loginUser = User.builder()
			.username(this.user1.getUsername())
			.password("teszt")
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

		task1.setOwnerid(user1.getId());
		task2.setOwnerid(user2.getId());
		task3.setOwnerid(user2.getId());
		task4.setOwnerid(user2.getId());

		taskService.saveTask(task1);
		taskService.saveTask(task2);
		taskService.saveTask(task3);
		taskService.saveTask(task4);

		keyword1.setTaskid(task1.getId());
		keyword2.setTaskid(task1.getId());
		keyword3.setTaskid(task2.getId());

		keywordsForTasksRepository.save(keyword1);
		keywordsForTasksRepository.save(keyword2);
		keywordsForTasksRepository.save(keyword3);

		if (this.jwtToken == null)
		{
			login();
		}
	}

	@After
	public void teardown()
	{
		taskService.deleteAll();
		userService.deleteAll();
	}

	@Test
	public void addTaskTest()
	{
		Task testTask = Task.builder()
			.name("tarea de ejemplo")
			.description("descripción")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user1.getId())
			.build();

		HttpHeaders headersPost = new HttpHeaders();
		headersPost.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headersPost.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headersPost.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Task> requestBodyWithHeaders = new HttpEntity<>(testTask, headersPost);
		ResponseEntity<Task> responseEntity = this.restTemplate.postForEntity(baseEndpoint, requestBodyWithHeaders, Task.class);

		Task[] tasksInDb = this.restTemplate.exchange(baseEndpoint, HttpMethod.GET, new HttpEntity<>(headersPost), Task[].class).getBody();

		Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		Assert.assertNotNull(tasksInDb);
		Assert.assertEquals(5, tasksInDb.length);
		Assert.assertEquals(testTask.getName(), tasksInDb[4].getName());
		Assert.assertEquals(testTask.getDescription(), tasksInDb[4].getDescription());
		Assert.assertEquals(testTask.getTimeofcreation(), tasksInDb[4].getTimeofcreation());
		Assert.assertEquals(testTask.getMaintaskid(), tasksInDb[4].getMaintaskid());
		Assert.assertEquals(testTask.getOwnerid(), tasksInDb[4].getOwnerid());
	}

	@Test
	public void addTaskWithNullValuesTest()
	{
		Task testTask = Task.builder()
			.name("Krumplisteszta")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Task> requestBodyWithHeaders = new HttpEntity<>(testTask, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(baseEndpoint, requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addTaskWithIdTest()
	{
		Task testTask = Task.builder()
			.id(1L)
			.name("Krumplisteszta")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Task> requestBodyWithHeaders = new HttpEntity<>(testTask, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(baseEndpoint, requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addNullTaskTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Task> requestBodyWithHeaders = new HttpEntity<>(null, headers);
		ResponseEntity<HttpErrorResponseForTests> response = restTemplate.postForEntity(baseEndpoint, requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addTaskWithInvalidOwnerIdTest()
	{
		Task testTask = Task.builder()
			.name("tarea de ejemplo")
			.description("descripción")
			.timeofcreation(LocalDate.now())
			.ownerid(Long.MAX_VALUE)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Task> requestBodyWithHeaders = new HttpEntity<>(testTask, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(baseEndpoint, requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Not Found", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addTaskWithoutOwnerIdTest()
	{
		Task testTask = Task.builder()
			.name("tarea de ejemplo")
			.description("descripción")
			.timeofcreation(LocalDate.now())
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Task> requestBodyWithHeaders = new HttpEntity<>(testTask, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(baseEndpoint, requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void getSingleTaskTest()
	{
		Task queryTask = Task.builder()
			.id(this.task1.getId())
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		String paramsURI = "?id={id}" +
			"&name={name}" +
			"&description={description}" +
			"&maintaskid={maintaskid}" +
			"&ownerid={ownerid}";

		ResponseEntity<Task[]> responseEntity = restTemplate.exchange(baseEndpoint + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			Task[].class,
			queryTask.getId(),
			queryTask.getName(),
			queryTask.getDescription(),
			queryTask.getMaintaskid(),
			queryTask.getOwnerid()
		);

		Task[] tasksAccordingToQuery = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(tasksAccordingToQuery);
		Assert.assertEquals(1, tasksAccordingToQuery.length);
		Assert.assertEquals(task1, tasksAccordingToQuery[0]);
	}

	@Test
	public void getMultipleTasksTest()
	{
		Task queryTask = Task.builder()
			.timeofcreation(LocalDate.EPOCH)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		String paramsURI = "?createdafter={timeofcreation}&createdbefore={timeofcreation}";

		ResponseEntity<TaskListingFilter[]> responseEntity = restTemplate.exchange(baseEndpoint + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			TaskListingFilter[].class,
			queryTask.getTimeofcreation(),
			queryTask.getTimeofcreation()
		);

		TaskListingFilter[] tasksAccordingToQuery = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(tasksAccordingToQuery);
		Assert.assertEquals(2, tasksAccordingToQuery.length);
		Assert.assertEquals(task2.getId(), tasksAccordingToQuery[0].getId());
		Assert.assertEquals(task2.getName(), tasksAccordingToQuery[0].getName());
		Assert.assertEquals(task2.getDescription(), tasksAccordingToQuery[0].getDescription());
		Assert.assertEquals(task2.getTimeofcreation(), tasksAccordingToQuery[0].getTimeofcreation());
		Assert.assertEquals(task2.getMaintaskid(), tasksAccordingToQuery[1].getMaintaskid());
		Assert.assertEquals(task2.getOwnerid(), tasksAccordingToQuery[1].getOwnerid());
		Assert.assertEquals(task3.getId(), tasksAccordingToQuery[1].getId());
		Assert.assertEquals(task3.getName(), tasksAccordingToQuery[1].getName());
		Assert.assertEquals(task3.getDescription(), tasksAccordingToQuery[1].getDescription());
		Assert.assertEquals(task3.getTimeofcreation(), tasksAccordingToQuery[1].getTimeofcreation());
		Assert.assertEquals(task3.getMaintaskid(), tasksAccordingToQuery[1].getMaintaskid());
		Assert.assertEquals(task3.getOwnerid(), tasksAccordingToQuery[1].getOwnerid());
	}

	@Test
	public void getAllTasksTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<Task[]> responseEntity = restTemplate.exchange(baseEndpoint,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			Task[].class
		);

		Task[] tasksInDb = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(tasksInDb);
		Assert.assertEquals(4, tasksInDb.length);
		Assert.assertEquals(task1, tasksInDb[0]);
		Assert.assertEquals(task2, tasksInDb[1]);
		Assert.assertEquals(task3, tasksInDb[2]);
		Assert.assertEquals(task4, tasksInDb[3]);
	}

	@Test
	public void getTasksByFilterTest()
	{
		KeywordsForTasks keyword4 = KeywordsForTasks.builder().taskid(task2.getId()).keyword("ABC").build();
		KeywordsForTasks keyword5 = KeywordsForTasks.builder().taskid(task2.getId()).keyword("DEF").build();
		KeywordsForTasks keyword6 = KeywordsForTasks.builder().taskid(task4.getId()).keyword("GHI").build();

		keywordsForTasksRepository.saveAll(List.of(keyword4, keyword5, keyword6));

		TaskListingFilter queryFilter = TaskListingFilter.builder()
			.name("Example Task")
			.keywords(List.of("ABC", "GHI"))
			.ownerid(this.user2.getId())
			.completedUserId(this.user1.getId())
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		String paramsURI = "?name={name}" +
			"&keywords={keyword1}" +
			"&keywords={keyword2}" +
			"&ownerid={ownerid}";

		ResponseEntity<Task[]> responseEntity = restTemplate.exchange(baseEndpoint + "/" + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			Task[].class,
			queryFilter.getName(),
			queryFilter.getKeywords().toArray()[0],
			queryFilter.getKeywords().toArray()[1],
			queryFilter.getOwnerid()
		);

		Task[] tasksAccordingToQuery = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(tasksAccordingToQuery);
		Assert.assertEquals(2, tasksAccordingToQuery.length);
		Assert.assertEquals(task2, tasksAccordingToQuery[0]);
		Assert.assertEquals(task4, tasksAccordingToQuery[1]);
	}

	@Test
	public void updateTaskTest()
	{
		Task propertiesToUpdate = Task.builder()
			.id(this.task1.getId())
			.name("tarea de ejemplo")
			.description("descripción")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user1.getId())
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<Task> responseEntity = restTemplate.exchange(baseEndpoint, HttpMethod.PUT, new HttpEntity<>(propertiesToUpdate, headers), Task.class);
		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ResponseEntity<Task[]> responseFromDb = this.restTemplate.exchange(baseEndpoint + "?id={id}", HttpMethod.GET, new HttpEntity<>(headers), Task[].class, this.task1.getId());
		Assert.assertEquals(HttpStatus.OK, responseFromDb.getStatusCode());
		Assert.assertNotNull(responseFromDb.getBody());
		Task taskFromDb = responseFromDb.getBody()[0];

		Assert.assertEquals(propertiesToUpdate, taskFromDb);
	}

	@Test
	public void updateTaskWithoutIdTest()
	{
		Task propertiesToUpdate = Task.builder()
			.name("potato pasta")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add(HttpHeaders.ACCEPT, "application/json");
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Task> requestBodyWithHeaders = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.exchange(baseEndpoint, HttpMethod.PUT, requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void updateTaskWithInvalidIdTest()
	{
		Task propertiesToUpdate = Task.builder()
			.id(Long.MAX_VALUE)
			.name("potato pasta")
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add(HttpHeaders.ACCEPT, "application/json");
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Task> requestBodyWithHeaders = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.exchange(baseEndpoint, HttpMethod.PUT, requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Not Found", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void updateTaskToInvalidOwnerIdTest()
	{
		Task propertiesToUpdate = Task.builder()
			.id(this.task1.getId())
			.name("potato pasta")
			.ownerid(Long.MAX_VALUE)
			.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
		headers.add(HttpHeaders.ACCEPT, "application/json");
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Task> requestBodyWithHeaders = new HttpEntity<>(propertiesToUpdate, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.exchange(baseEndpoint, HttpMethod.PUT, requestBodyWithHeaders, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Not Found", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void deleteTaskTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<Void> deleteResponse = this.restTemplate.exchange(baseEndpoint + this.task1.getId(), HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

		ResponseEntity<Task[]> responseEntity = this.restTemplate.exchange(baseEndpoint, HttpMethod.GET, new HttpEntity<>(headers), Task[].class);

		Task[] tasksFromDb = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(tasksFromDb);
		Assert.assertEquals(3, tasksFromDb.length);
		Assert.assertEquals(task2, tasksFromDb[0]);
		Assert.assertEquals(task3, tasksFromDb[1]);
		Assert.assertEquals(task4, tasksFromDb[2]);
	}

	@Test
	public void deleteTaskWithNonExistingIdTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.exchange(baseEndpoint + "-1", HttpMethod.DELETE, new HttpEntity<>(headers), HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Not Found", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addKeywordTest()
	{
		KeywordsForTasks keyword4 = KeywordsForTasks.builder().taskid(task2.getId()).keyword("ABC").build();
		KeywordsForTasks keyword5 = KeywordsForTasks.builder().taskid(task2.getId()).keyword("DEF").build();
		KeywordsForTasks keyword6 = KeywordsForTasks.builder().taskid(task4.getId()).keyword("GHI").build();

		List<KeywordsForTasks> keywordList = List.of(keyword4, keyword5, keyword6);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Iterable<KeywordsForTasks>> requestEntity = new HttpEntity<>(keywordList, headers);
		ResponseEntity<KeywordsForTasks[]> responseEntity = this.restTemplate.postForEntity(keywordsEndpoint, requestEntity, KeywordsForTasks[].class);

		KeywordsForTasks[] keywordsInDb = this.restTemplate.exchange(keywordsEndpoint, HttpMethod.GET, new HttpEntity<>(headers), KeywordsForTasks[].class).getBody();

		Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		Assert.assertNotNull(responseEntity.getBody());
		Assert.assertNotNull(keywordsInDb);
		Assert.assertEquals(6, keywordsInDb.length);
		Assert.assertEquals(keyword4.getTaskid(), keywordsInDb[3].getTaskid());
		Assert.assertEquals(keyword4.getKeyword(), keywordsInDb[3].getKeyword());
		Assert.assertEquals(keyword5.getTaskid(), keywordsInDb[4].getTaskid());
		Assert.assertEquals(keyword5.getKeyword(), keywordsInDb[4].getKeyword());
		Assert.assertEquals(keyword6.getTaskid(), keywordsInDb[5].getTaskid());
		Assert.assertEquals(keyword6.getKeyword(), keywordsInDb[5].getKeyword());
		Assert.assertEquals(responseEntity.getBody()[0], keywordsInDb[3]);
		Assert.assertEquals(responseEntity.getBody()[1], keywordsInDb[4]);
		Assert.assertEquals(responseEntity.getBody()[2], keywordsInDb[5]);
	}

	@Test
	public void addKeywordWithNullValuesTest()
	{
		KeywordsForTasks keyword = KeywordsForTasks.builder().keyword("ABC").build();

		List<KeywordsForTasks> keywordList = List.of(keyword);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Iterable<KeywordsForTasks>> requestEntity = new HttpEntity<>(keywordList, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(keywordsEndpoint, requestEntity, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addKeywordWithIdTest()
	{
		KeywordsForTasks keyword = KeywordsForTasks.builder().id(1L).taskid(this.task1.getId()).keyword("ABC").build();

		List<KeywordsForTasks> keywordList = List.of(keyword);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Iterable<KeywordsForTasks>> requestEntity = new HttpEntity<>(keywordList, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(keywordsEndpoint, requestEntity, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void addNullKeywordTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		HttpEntity<Iterable<KeywordsForTasks>> requestEntity = new HttpEntity<>(null, headers);
		ResponseEntity<HttpErrorResponseForTests> response = this.restTemplate.postForEntity(keywordsEndpoint, requestEntity, HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Assert.assertNotNull(response.getBody());
		Assert.assertEquals("Bad Request", response.getBody().getError());
		Assert.assertNotNull(response.getBody().getMessage());
	}

	@Test
	public void getSingleKeywordTest()
	{
		String paramsURI = "?id={id}";

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<KeywordsForTasks[]> responseEntity = this.restTemplate.exchange(keywordsEndpoint + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			KeywordsForTasks[].class,
			this.keyword1.getId()
		);

		KeywordsForTasks[] keywords = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(keywords);
		Assert.assertEquals(1, keywords.length);
		Assert.assertEquals(keyword1, keywords[0]);
	}

	@Test
	public void getMultipleKeywordsByKeywordTest()
	{
		String paramsURI = "?&keyword={keyword}";

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<KeywordsForTasks[]> responseEntity = this.restTemplate.exchange(keywordsEndpoint + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			KeywordsForTasks[].class,
			"ABC"
		);

		KeywordsForTasks[] keywords = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(keywords);
		Assert.assertEquals(2, keywords.length);
		Assert.assertEquals(keyword1, keywords[0]);
		Assert.assertEquals(keyword3, keywords[1]);
	}

	@Test
	public void getMultipleKeywordsByTaskIdTest()
	{
		String paramsURI = "?&taskid={taskid}";

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<KeywordsForTasks[]> responseEntity = this.restTemplate.exchange(keywordsEndpoint + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			KeywordsForTasks[].class,
			this.task1.getId()
		);

		KeywordsForTasks[] keywords = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(keywords);
		Assert.assertEquals(2, keywords.length);
		Assert.assertEquals(keyword1, keywords[0]);
		Assert.assertEquals(keyword2, keywords[1]);
	}

	@Test
	public void deleteKeywordTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<Void> deleteResponse = this.restTemplate.exchange(keywordsEndpoint + this.keyword1.getId(), HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

		KeywordsForTasks[] keywordsFromDb = this.restTemplate.exchange(keywordsEndpoint, HttpMethod.GET, new HttpEntity<>(headers), KeywordsForTasks[].class).getBody();

		Assert.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
		Assert.assertNotNull(keywordsFromDb);
		Assert.assertEquals(2, keywordsFromDb.length);
		Assert.assertEquals(keyword2, keywordsFromDb[0]);
		Assert.assertEquals(keyword3, keywordsFromDb[1]);
	}

	@Test
	public void deleteKeywordWithNonExistingIdTest()
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwtToken);

		ResponseEntity<HttpErrorResponseForTests> deleteResponse = this.restTemplate.exchange(keywordsEndpoint + "-1", HttpMethod.DELETE, new HttpEntity<>(headers), HttpErrorResponseForTests.class);

		Assert.assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());
		Assert.assertNotNull(deleteResponse.getBody());
		Assert.assertEquals("Not Found", deleteResponse.getBody().getError());
		Assert.assertNotNull(deleteResponse.getBody().getMessage());
	}
}
