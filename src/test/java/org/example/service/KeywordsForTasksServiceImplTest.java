package org.example.service;

import org.example.AbstractTest;
import org.example.model.KeywordsForTasks;
import org.example.model.Task;
import org.example.model.User;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.utils.UserStatusEnum;
import org.example.utils.exceptions.ServiceException;
import org.example.utils.exceptions.ServiceExceptionType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

public class KeywordsForTasksServiceImplTest extends AbstractTest
{

	@Autowired
	private KeywordsForTasksService keywordsForTasksService;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserRepository userRepository;

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
		userRepository.save(user);
		this.task.setOwnerid(user.getId());
		taskRepository.save(task);
	}

	@After
	public void tearDown()
	{
		keywordsForTasksService.deleteAllKeywordsForAllTasks();
		taskRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	public void saveValidKeywordForTaskTest()
	{
		KeywordsForTasks keyword = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();

		keywordsForTasksService.saveKeywordForTask(keyword);

		Assert.assertNotNull(keywordsForTasksService.getKeywordForTaskById(keyword.getId()));
	}

	@Test
	public void saveInvalidKeywordForTaskTest()
	{

		KeywordsForTasks keyword1 = KeywordsForTasks.builder()
			.id(1L)
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();
		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> keywordsForTasksService.saveKeywordForTask(keyword1));
		Assert.assertEquals(ServiceExceptionType.ID_GIVEN, exception.getServiceExceptionTypeEnum());

		KeywordsForTasks keyword2 = KeywordsForTasks.builder()
			.taskid(Long.MAX_VALUE)
			.keyword("ABC")
			.build();
		exception = Assert.assertThrows(ServiceException.class, () -> keywordsForTasksService.saveKeywordForTask(keyword2));
		Assert.assertEquals(ServiceExceptionType.CONSTRAINT_VIOLATION, exception.getServiceExceptionTypeEnum());

		KeywordsForTasks keyword3 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.build();
		exception = Assert.assertThrows(ServiceException.class, () -> keywordsForTasksService.saveKeywordForTask(keyword3));
		Assert.assertEquals(ServiceExceptionType.NULL_ARGUMENT, exception.getServiceExceptionTypeEnum());

		KeywordsForTasks keyword4 = KeywordsForTasks.builder()
			.keyword("ABC")
			.build();
		exception = Assert.assertThrows(ServiceException.class, () -> keywordsForTasksService.saveKeywordForTask(keyword4));
		Assert.assertEquals(ServiceExceptionType.NULL_ARGUMENT, exception.getServiceExceptionTypeEnum());
	}

	@Test
	public void saveMultipleKeywordsForTaskTest()
	{
		KeywordsForTasks keyword1 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();

		KeywordsForTasks keyword2 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("DEF")
			.build();

		KeywordsForTasks keyword3 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("GHI")
			.build();

		KeywordsForTasks keyword4 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("JKL")
			.build();
		keywordsForTasksService.saveKeywordsForTasks(List.of(keyword1, keyword2, keyword3, keyword4));

		Assert.assertNotNull(keywordsForTasksService.getKeywordForTaskById(keyword1.getId()));
		Assert.assertEquals(keyword1, keywordsForTasksService.getKeywordForTaskById(keyword1.getId()));

		Assert.assertNotNull(keywordsForTasksService.getKeywordForTaskById(keyword2.getId()));
		Assert.assertEquals(keyword2, keywordsForTasksService.getKeywordForTaskById(keyword2.getId()));

		Assert.assertNotNull(keywordsForTasksService.getKeywordForTaskById(keyword3.getId()));
		Assert.assertEquals(keyword3, keywordsForTasksService.getKeywordForTaskById(keyword3.getId()));

		Assert.assertNotNull(keywordsForTasksService.getKeywordForTaskById(keyword4.getId()));
		Assert.assertEquals(keyword4, keywordsForTasksService.getKeywordForTaskById(keyword4.getId()));
	}

	@Test
	public void getKeywordsForTaskByIdTest()
	{
		KeywordsForTasks keyword = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();

		keywordsForTasksService.saveKeywordForTask(keyword);
		Assert.assertEquals(keyword, keywordsForTasksService.getKeywordForTaskById(keyword.getId()));
	}

	@Test
	public void getKeywordsForTaskByObjectTest()
	{
		long oldTaskId = this.task.getId();
		this.task.setId(null);
		taskRepository.save(this.task);

		KeywordsForTasks keyword1 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();

		KeywordsForTasks keyword2 = KeywordsForTasks.builder()
			.taskid(oldTaskId)
			.keyword("DEF")
			.build();

		KeywordsForTasks keyword3 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("GHJ")
			.build();

		KeywordsForTasks keyword4 = KeywordsForTasks.builder()
			.taskid(oldTaskId)
			.keyword("ABC")
			.build();

		keywordsForTasksService.saveKeywordsForTasks(List.of(keyword1, keyword2, keyword3, keyword4));

		Assert.assertNotNull(keywordsForTasksService.getKeywordForTaskById(keyword1.getId()));
		Assert.assertNotNull(keywordsForTasksService.getKeywordForTaskById(keyword2.getId()));
		Assert.assertNotNull(keywordsForTasksService.getKeywordForTaskById(keyword3.getId()));
		Assert.assertNotNull(keywordsForTasksService.getKeywordForTaskById(keyword4.getId()));

		Spliterator<KeywordsForTasks> keywordsForTasksSpliterator = keywordsForTasksService.getByKeywordsForTasksObject(KeywordsForTasks.builder()
			.id(keyword1.getId())
			.build()
		).spliterator();
		Assert.assertTrue(StreamSupport.stream(keywordsForTasksSpliterator, false).allMatch(keyword1::equals));

		keywordsForTasksSpliterator = keywordsForTasksService.getByKeywordsForTasksObject(KeywordsForTasks.builder()
			.taskid(oldTaskId)
			.build()
		).spliterator();
		Assert.assertTrue(StreamSupport.stream(keywordsForTasksSpliterator, false).allMatch(keyword -> keyword2.equals(keyword) || keyword4.equals(keyword)));

		keywordsForTasksSpliterator = keywordsForTasksService.getByKeywordsForTasksObject(KeywordsForTasks.builder()
			.keyword("ABC")
			.build()
		).spliterator();
		Assert.assertTrue(StreamSupport.stream(keywordsForTasksSpliterator, false).allMatch(keyword -> keyword1.equals(keyword) || keyword4.equals(keyword)));

		keywordsForTasksSpliterator = keywordsForTasksService.getByKeywordsForTasksObject(null).spliterator();
		Assert.assertEquals(4, StreamSupport.stream(keywordsForTasksSpliterator, false).count());
	}

	@Test
	public void getAllKeywordsForTasksTest()
	{
		KeywordsForTasks keyword = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();
		keywordsForTasksService.saveKeywordForTask(keyword);

		keyword.setId(null);
		keywordsForTasksService.saveKeywordForTask(keyword);

		long count = StreamSupport.stream(keywordsForTasksService.getAllKeywordsForTasks().spliterator(), false).count();
		Assert.assertEquals(2, count);
	}

	@Test
	public void updateKeywordForTaskTest()
	{
		long oldTaskId = this.task.getId();
		this.task.setId(null);
		taskRepository.save(this.task);

		KeywordsForTasks keyword = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();
		keywordsForTasksService.saveKeywordForTask(keyword);
		Assert.assertEquals(keyword, keywordsForTasksService.getKeywordForTaskById(keyword.getId()));

		KeywordsForTasks updateKeywordForTaskProperties = KeywordsForTasks.builder()
			.id(keyword.getId())
			.taskid(oldTaskId)
			.build();
		keywordsForTasksService.updateKeywordForTask(updateKeywordForTaskProperties);
		Assert.assertEquals(updateKeywordForTaskProperties.getTaskid(), keywordsForTasksService.getKeywordForTaskById(keyword.getId()).getTaskid());

		updateKeywordForTaskProperties = KeywordsForTasks.builder()
			.id(keyword.getId())
			.keyword("DEF")
			.build();
		keywordsForTasksService.updateKeywordForTask(updateKeywordForTaskProperties);
		Assert.assertEquals(updateKeywordForTaskProperties.getKeyword(), keywordsForTasksService.getKeywordForTaskById(keyword.getId()).getKeyword());
	}

	@Test
	public void updateKeywordForTaskWithoutValidIdTest()
	{
		KeywordsForTasks keyword1 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();
		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> keywordsForTasksService.updateKeywordForTask(keyword1));
		Assert.assertEquals(ServiceExceptionType.ID_NOT_GIVEN, exception.getServiceExceptionTypeEnum());

		KeywordsForTasks keyword2 = KeywordsForTasks.builder()
			.id(Long.MAX_VALUE)
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();
		exception = Assert.assertThrows(ServiceException.class, () -> keywordsForTasksService.updateKeywordForTask(keyword2));
		Assert.assertEquals(ServiceExceptionType.ID_NOT_FOUND, exception.getServiceExceptionTypeEnum());
	}

	@Test
	public void deleteKeywordForTaskTest()
	{
		KeywordsForTasks keyword = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();
		keywordsForTasksService.saveKeywordForTask(keyword);
		Assert.assertEquals(keyword, keywordsForTasksService.getKeywordForTaskById(keyword.getId()));

		keywordsForTasksService.deleteKeywordForTask(keyword.getId());

		Assert.assertNull(keywordsForTasksService.getKeywordForTaskById(keyword.getId()));
	}

	@Test
	public void deleteKeywordTest()
	{
		KeywordsForTasks keyword1 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();

		KeywordsForTasks keyword2 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();

		KeywordsForTasks keyword3 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("DEF")
			.build();

		keywordsForTasksService.saveKeywordsForTasks(List.of(keyword1, keyword2, keyword3));
		Assert.assertEquals(3, StreamSupport.stream(keywordsForTasksService.getAllKeywordsForTasks().spliterator(), false).count());

		keywordsForTasksService.deleteKeyword("ABC");
		Assert.assertTrue(StreamSupport.stream(keywordsForTasksService.getAllKeywordsForTasks().spliterator(), false).allMatch(keyword3::equals));
	}

	@Test
	public void deleteAllKeywordsForTasksTest()
	{
		KeywordsForTasks keyword1 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("ABC")
			.build();

		KeywordsForTasks keyword2 = KeywordsForTasks.builder()
			.taskid(this.task.getId())
			.keyword("DEF")
			.build();

		keywordsForTasksService.saveKeywordsForTasks(List.of(keyword1, keyword2));
		Assert.assertEquals(keyword1, keywordsForTasksService.getKeywordForTaskById(keyword1.getId()));
		Assert.assertEquals(keyword2, keywordsForTasksService.getKeywordForTaskById(keyword2.getId()));

		keywordsForTasksService.deleteAllKeywordsForAllTasks();
		Assert.assertNull(keywordsForTasksService.getKeywordForTaskById(keyword1.getId()));
		Assert.assertNull(keywordsForTasksService.getKeywordForTaskById(keyword2.getId()));
	}
}
