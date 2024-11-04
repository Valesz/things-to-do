package org.example.repository;

import org.example.AbstractTest;
import org.example.model.KeywordsForTasks;
import org.example.model.Task;
import org.example.model.User;
import org.example.utils.enums.UserStatusEnum;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

public class KeywordsForTasksRepositoryTest extends AbstractTest
{

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private KeywordsForTasksRepository keywordsForTasksRepository;

	private final User user = User.builder()
		.username("teszt elek")
		.email("teszt@teszt.teszt")
		.timeofcreation(LocalDate.now())
		.status(UserStatusEnum.AKTIV)
		.password("teszt")
		.classification(0.5)
		.precisionofanswers(0.8)
		.build();

	private final Task task = Task.builder()
		.name("Pelda Task")
		.description("Leiras")
		.timeofcreation(LocalDate.now())
		.build();

	@Before
	public void setUp()
	{
		userRepository.save(this.user);
		this.task.setOwnerid(user.getId());
		taskRepository.save(this.task);
	}

	@After
	public void tearDown()
	{
		keywordsForTasksRepository.deleteAll();
		taskRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	public void addValidKeywordForTaskTest()
	{
		KeywordsForTasks keywordsForTasks = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("example keyword")
			.build();

		KeywordsForTasks savedKeywordForTask = keywordsForTasksRepository.save(keywordsForTasks);

		Assert.assertNotNull(savedKeywordForTask);
		Assert.assertEquals(keywordsForTasks, savedKeywordForTask);
	}

	@Test
	public void addInvalidTaskIdKeywordForTaskTest()
	{
		KeywordsForTasks keywordsForTasks = KeywordsForTasks.builder()
			.taskid(Long.MAX_VALUE)
			.keyword("example keyword")
			.build();

		Assert.assertThrows(DbActionExecutionException.class, () -> keywordsForTasksRepository.save(keywordsForTasks));
	}

	@Test
	public void addTooLongKeywordTest()
	{
		KeywordsForTasks keywordsForTasks = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("other example keyword")
			.build();

		Assert.assertThrows(DbActionExecutionException.class, () -> keywordsForTasksRepository.save(keywordsForTasks));
	}

	@Test
	public void addKeywordForTaskWithMissingDataTest()
	{
		KeywordsForTasks keywordsForTasks = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.build();

		Assert.assertThrows(DbActionExecutionException.class, () -> keywordsForTasksRepository.save(keywordsForTasks));
	}

	@Test
	public void addEmptyKeywordForTaskTest()
	{
		KeywordsForTasks keywordsForTasks = new KeywordsForTasks();

		Assert.assertThrows(DbActionExecutionException.class, () -> keywordsForTasksRepository.save(keywordsForTasks));
	}

	@Test
	public void getKeywordsForTaskTest()
	{
		KeywordsForTasks keywordsForTasks = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("example keyword")
			.build();

		keywordsForTasksRepository.save(keywordsForTasks);

		Assert.assertEquals(keywordsForTasks, keywordsForTasksRepository.findById(keywordsForTasks.getTaskid()).orElse(null));
	}

	@Test
	public void getMultipleKeywordsForTaskTest()
	{
		KeywordsForTasks keywordsForTasks1 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("example keyword")
			.build();

		KeywordsForTasks keywordsForTasks2 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("other keyword")
			.build();

		keywordsForTasksRepository.save(keywordsForTasks1);
		keywordsForTasksRepository.save(keywordsForTasks2);

		List<KeywordsForTasks> keywordsForTasksList = StreamSupport.stream(keywordsForTasksRepository.findAll().spliterator(), false).toList();

		Assert.assertEquals(2, keywordsForTasksList.size());
	}

	@Test
	public void getNonExistingKeywordForTaskTest()
	{
		Assert.assertNull(keywordsForTasksRepository.findById(Long.MAX_VALUE).orElse(null));
		Assert.assertNull(keywordsForTasksRepository.findById(-1L).orElse(null));
		Assert.assertNull(keywordsForTasksRepository.findById(1L).orElse(null));
	}

	@Test
	public void deleteKeywordForTaskTest()
	{
		KeywordsForTasks keywordsForTasks = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("example keyword")
			.build();

		keywordsForTasksRepository.save(keywordsForTasks);

		Assert.assertNotNull(keywordsForTasksRepository.findById(keywordsForTasks.getTaskid()).orElse(null));

		keywordsForTasksRepository.delete(keywordsForTasks);

		Assert.assertNull(keywordsForTasksRepository.findById(keywordsForTasks.getTaskid()).orElse(null));
	}

	@Test
	public void deleteKeywordTest()
	{
		Task taskInner = Task.builder()
			.name("Pelda Task 2")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		taskRepository.save(taskInner);

		KeywordsForTasks keywordsForTasks1 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("example keyword")
			.build();

		KeywordsForTasks keywordsForTasks2 = KeywordsForTasks.builder()
			.taskid(taskInner.getId())
			.keyword("example keyword")
			.build();

		keywordsForTasksRepository.save(keywordsForTasks1);
		keywordsForTasksRepository.save(keywordsForTasks2);

		Assert.assertEquals(2, keywordsForTasksRepository.deleteKeyword("example keyword"));

		Assert.assertNull(keywordsForTasksRepository.findById(keywordsForTasks1.getTaskid()).orElse(null));
		Assert.assertNull(keywordsForTasksRepository.findById(keywordsForTasks2.getTaskid()).orElse(null));
	}
}
