package org.example.controller;

import org.example.AbstractTest;
import org.example.model.CompletedTask;
import org.example.model.KeywordsForTasks;
import org.example.model.Task;
import org.example.model.User;
import org.example.repository.CompletedTasksRepository;
import org.example.repository.KeywordsForTasksRepository;
import org.example.service.Filter;
import org.example.service.TaskService;
import org.example.service.UserService;
import org.example.utils.UserStatusEnum;
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
	private CompletedTasksRepository completedTasksRepository;

	@Autowired
	private TestRestTemplate restTemplate;

	private final String baseEndpoint = "/api/task/";

	private final String keywordsEndpoint = baseEndpoint + "keyword/";
	private final String completedTaskEndpoint = baseEndpoint + "completed/";

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

	CompletedTask completedTask1 = new CompletedTask();
	CompletedTask completedTask2 = new CompletedTask();
	CompletedTask completedTask3 = new CompletedTask();

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

		completedTask1.setTaskid(task1.getId());
		completedTask1.setUserid(user1.getId());
		completedTask2.setTaskid(task1.getId());
		completedTask2.setUserid(user2.getId());
		completedTask3.setTaskid(task2.getId());
		completedTask3.setUserid(user1.getId());

		completedTasksRepository.save(completedTask1);
		completedTasksRepository.save(completedTask2);
		completedTasksRepository.save(completedTask3);
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

		HttpEntity<Task> requestBodyWithHeaders = new HttpEntity<>(testTask, headersPost);
		ResponseEntity<Task> responseEntity = this.restTemplate.postForEntity(baseEndpoint, requestBodyWithHeaders, Task.class);

		Task[] tasksInDb = this.restTemplate.getForEntity(baseEndpoint, Task[].class).getBody();

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
	public void getSingleTaskTest()
	{
		Task queryTask = Task.builder()
			.id(this.task1.getId())
			.build();

		HttpHeaders headers = new HttpHeaders();

		String paramsURI = "?id={id}" +
			"&name={name}" +
			"&description={description}" +
			"&timeofcreation={timeofcreation}" +
			"&maintaskid={maintaskid}" +
			"&ownerid={ownerid}";

		ResponseEntity<Task[]> responseEntity = restTemplate.exchange(baseEndpoint + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			Task[].class,
			queryTask.getId(),
			queryTask.getName(),
			queryTask.getDescription(),
			queryTask.getTimeofcreation(),
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

		String paramsURI = "?timeofcreation={timeofcreation}";

		ResponseEntity<Task[]> responseEntity = restTemplate.exchange(baseEndpoint + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			Task[].class,
			queryTask.getTimeofcreation()
		);

		Task[] tasksAccordingToQuery = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(tasksAccordingToQuery);
		Assert.assertEquals(2, tasksAccordingToQuery.length);
		Assert.assertEquals(task2, tasksAccordingToQuery[0]);
		Assert.assertEquals(task3, tasksAccordingToQuery[1]);
	}

	@Test
	public void getAllTasksTest()
	{
		HttpHeaders headers = new HttpHeaders();

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

		CompletedTask completedTask1 = CompletedTask.builder().taskid(task1.getId()).userid(this.user1.getId()).build();
		CompletedTask completedTask2 = CompletedTask.builder().taskid(task1.getId()).userid(this.user2.getId()).build();
		CompletedTask completedTask3 = CompletedTask.builder().taskid(task2.getId()).userid(this.user1.getId()).build();
		CompletedTask completedTask4 = CompletedTask.builder().taskid(task3.getId()).userid(this.user2.getId()).build();
		CompletedTask completedTask5 = CompletedTask.builder().taskid(task4.getId()).userid(this.user1.getId()).build();
		CompletedTask completedTask6 = CompletedTask.builder().taskid(task4.getId()).userid(this.user2.getId()).build();

		completedTasksRepository.saveAll(List.of(completedTask1, completedTask2, completedTask3, completedTask4, completedTask5, completedTask6));

		Filter queryFilter = Filter.builder()
			.name("Example Task")
			.keywords(List.of("ABC", "GHI"))
			.ownerId(this.user2.getId())
			.completedUserId(this.user1.getId())
			.build();

		HttpHeaders headers = new HttpHeaders();

		String paramsURI = "?name={name}" +
			"&keywords={keyword1}" +
			"&keywords={keyword2}" +
			"&ownerid={ownerid}" +
			"&completeduserId={completedUserId}";

		ResponseEntity<Task[]> responseEntity = restTemplate.exchange(baseEndpoint + "/filter" + paramsURI,
			HttpMethod.GET,
			new HttpEntity<>(headers),
			Task[].class,
			queryFilter.getName(),
			queryFilter.getKeywords().toArray()[0],
			queryFilter.getKeywords().toArray()[1],
			queryFilter.getOwnerId(),
			queryFilter.getCompletedUserId()
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

		ResponseEntity<Task> responseEntity = restTemplate.exchange(baseEndpoint, HttpMethod.PUT, new HttpEntity<>(propertiesToUpdate), Task.class);
		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		ResponseEntity<Task[]> responseFromDb = this.restTemplate.getForEntity(baseEndpoint + "?id={id}", Task[].class, this.task1.getId());
		Assert.assertEquals(HttpStatus.OK, responseFromDb.getStatusCode());
		Assert.assertNotNull(responseFromDb.getBody());
		Task taskFromDb = responseFromDb.getBody()[0];

		Assert.assertEquals(propertiesToUpdate, taskFromDb);
	}

	@Test
	public void deleteTaskTest()
	{
		ResponseEntity<Void> deleteResponse = this.restTemplate.exchange(baseEndpoint + this.task1.getId(), HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

		ResponseEntity<Task[]> responseEntity = this.restTemplate.getForEntity(baseEndpoint, Task[].class);

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
	public void addKeywordTest()
	{
		KeywordsForTasks keyword4 = KeywordsForTasks.builder().taskid(task2.getId()).keyword("ABC").build();
		KeywordsForTasks keyword5 = KeywordsForTasks.builder().taskid(task2.getId()).keyword("DEF").build();
		KeywordsForTasks keyword6 = KeywordsForTasks.builder().taskid(task4.getId()).keyword("GHI").build();

		List<KeywordsForTasks> keywordList = List.of(keyword4, keyword5, keyword6);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<Iterable<KeywordsForTasks>> requestEntity = new HttpEntity<>(keywordList, headers);
		ResponseEntity<KeywordsForTasks[]> responseEntity = this.restTemplate.postForEntity(keywordsEndpoint, requestEntity, KeywordsForTasks[].class);

		KeywordsForTasks[] keywordsInDb = this.restTemplate.getForEntity(keywordsEndpoint, KeywordsForTasks[].class).getBody();

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
	public void getSingleKeywordTest()
	{
		String paramsURI = "?id={id}";

		ResponseEntity<KeywordsForTasks[]> responseEntity = this.restTemplate.getForEntity(keywordsEndpoint + paramsURI,
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

		ResponseEntity<KeywordsForTasks[]> responseEntity = this.restTemplate.getForEntity(keywordsEndpoint + paramsURI,
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

		ResponseEntity<KeywordsForTasks[]> responseEntity = this.restTemplate.getForEntity(keywordsEndpoint + paramsURI,
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
		ResponseEntity<Void> deleteResponse = this.restTemplate.exchange(keywordsEndpoint + this.keyword1.getId(), HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

		KeywordsForTasks[] keywordsFromDb = this.restTemplate.getForEntity(keywordsEndpoint, KeywordsForTasks[].class).getBody();

		Assert.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
		Assert.assertNotNull(keywordsFromDb);
		Assert.assertEquals(2, keywordsFromDb.length);
		Assert.assertEquals(keyword2, keywordsFromDb[0]);
		Assert.assertEquals(keyword3, keywordsFromDb[1]);
	}

	@Test
	public void addCompletedTasksTest()
	{
		CompletedTask testCompletedTask = CompletedTask.builder().taskid(task3.getId()).userid(this.user2.getId()).build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<CompletedTask> requestEntity = new HttpEntity<>(testCompletedTask, headers);
		ResponseEntity<CompletedTask> response = this.restTemplate.postForEntity(completedTaskEndpoint, requestEntity, CompletedTask.class);
		CompletedTask completedTaskFromResponse = response.getBody();

		CompletedTask[] completedTasksFromDb = this.restTemplate.getForEntity(completedTaskEndpoint, CompletedTask[].class).getBody();

		Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
		Assert.assertNotNull(completedTaskFromResponse);
		Assert.assertNotNull(completedTasksFromDb);
		Assert.assertEquals(completedTaskFromResponse, completedTasksFromDb[3]);
		Assert.assertEquals(testCompletedTask.getTaskid(), completedTaskFromResponse.getTaskid());
		Assert.assertEquals(testCompletedTask.getUserid(), completedTaskFromResponse.getUserid());
	}

	@Test
	public void getSingleCompletedTaskTest()
	{
		String paramsURI = "?id={id}";

		ResponseEntity<CompletedTask[]> responseEntity = this.restTemplate.getForEntity(completedTaskEndpoint + paramsURI,
			CompletedTask[].class,
			this.completedTask1.getId()
		);

		Assert.assertNotNull(responseEntity.getBody());
		CompletedTask completedTaskFromDb = responseEntity.getBody()[0];

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(completedTaskFromDb);
		Assert.assertEquals(this.completedTask1, completedTaskFromDb);
	}

	@Test
	public void getMultipleCompletedTasksByTaskIdTest()
	{
		String paramsURI = "?&taskid={taskid}";

		ResponseEntity<CompletedTask[]> responseEntity = this.restTemplate.getForEntity(completedTaskEndpoint + paramsURI,
			CompletedTask[].class,
			this.task1.getId()
		);

		CompletedTask[] completedTasksFromDb = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(completedTasksFromDb);
		Assert.assertEquals(2, completedTasksFromDb.length);
		Assert.assertEquals(this.completedTask1, completedTasksFromDb[0]);
		Assert.assertEquals(this.completedTask2, completedTasksFromDb[1]);
	}

	@Test
	public void getMultipleCompletedTasksByUserIdTest()
	{
		String paramsURI = "?&userid={userid}";

		ResponseEntity<CompletedTask[]> responseEntity = this.restTemplate.getForEntity(completedTaskEndpoint + paramsURI,
			CompletedTask[].class,
			this.user1.getId()
		);

		CompletedTask[] completedTasksFromDb = responseEntity.getBody();

		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertNotNull(completedTasksFromDb);
		Assert.assertEquals(2, completedTasksFromDb.length);
		Assert.assertEquals(this.completedTask1, completedTasksFromDb[0]);
		Assert.assertEquals(this.completedTask3, completedTasksFromDb[1]);
	}

	@Test
	public void getAllCompletedTasksTest()
	{
		ResponseEntity<CompletedTask[]> getAllResponse = this.restTemplate.getForEntity(completedTaskEndpoint, CompletedTask[].class);

		CompletedTask[] completedTasksFromDb = getAllResponse.getBody();

		Assert.assertEquals(HttpStatus.OK, getAllResponse.getStatusCode());
		Assert.assertNotNull(completedTasksFromDb);
		Assert.assertEquals(3, completedTasksFromDb.length);
		Assert.assertEquals(this.completedTask1, completedTasksFromDb[0]);
		Assert.assertEquals(this.completedTask2, completedTasksFromDb[1]);
		Assert.assertEquals(this.completedTask3, completedTasksFromDb[2]);
	}

	@Test
	public void deleteCompletedTaskTest()
	{
		ResponseEntity<Void> deleteResponse = this.restTemplate.exchange(completedTaskEndpoint + this.completedTask1.getId(), HttpMethod.DELETE, new HttpEntity<>(null), Void.class);

		ResponseEntity<CompletedTask[]> getAllResponse = this.restTemplate.getForEntity(completedTaskEndpoint, CompletedTask[].class);

		CompletedTask[] completedTasksFromDb = getAllResponse.getBody();

		Assert.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
		Assert.assertNotNull(completedTasksFromDb);
		Assert.assertEquals(2, completedTasksFromDb.length);
		Assert.assertEquals(this.completedTask2, completedTasksFromDb[0]);
		Assert.assertEquals(this.completedTask3, completedTasksFromDb[1]);
	}
}
