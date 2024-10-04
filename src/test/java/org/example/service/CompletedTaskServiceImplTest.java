package org.example.service;

import org.example.AbstractTest;
import org.example.model.CompletedTask;
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
import java.util.Spliterator;
import java.util.stream.StreamSupport;

public class CompletedTaskServiceImplTest extends AbstractTest
{

	@Autowired
	private CompletedTasksService completedTasksService;

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
		this.taskRepository.save(task);
	}

	@After
	public void teardown()
	{
		completedTasksService.deleteAll();
		taskRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	public void saveValidCompletedTaskTest()
	{
		CompletedTask completedTask = CompletedTask.builder()
			.userid(this.user.getId())
			.taskid(this.task.getId())
			.build();

		completedTasksService.saveCompletedTask(completedTask);
		Assert.assertEquals(completedTask, completedTasksService.getCompletedTaskById(completedTask.getId()));
	}

	@Test
	public void saveInvalidCompletedTaskTest()
	{
		CompletedTask completedTask1 = CompletedTask.builder()
			.id(1L)
			.userid(this.user.getId())
			.taskid(this.task.getId())
			.build();

		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> completedTasksService.saveCompletedTask(completedTask1));
		Assert.assertEquals(ServiceExceptionType.ILLEGAL_ID_ARGUMENT, exception.getServiceExceptionTypeEnum());

		CompletedTask completedTask2 = CompletedTask.builder()
			.taskid(this.task.getId())
			.build();

		exception = Assert.assertThrows(ServiceException.class, () -> completedTasksService.saveCompletedTask(completedTask2));
		Assert.assertEquals(ServiceExceptionType.NULL_ARGUMENT, exception.getServiceExceptionTypeEnum());

		CompletedTask completedTask3 = CompletedTask.builder()
			.userid(this.user.getId())
			.build();

		exception = Assert.assertThrows(ServiceException.class, () -> completedTasksService.saveCompletedTask(completedTask3));
		Assert.assertEquals(ServiceExceptionType.NULL_ARGUMENT, exception.getServiceExceptionTypeEnum());

		CompletedTask completedTask4 = CompletedTask.builder()
			.userid(Long.MAX_VALUE)
			.taskid(this.task.getId())
			.build();

		exception = Assert.assertThrows(ServiceException.class, () -> completedTasksService.saveCompletedTask(completedTask4));
		Assert.assertEquals(ServiceExceptionType.CONSTRAINT_VIOLATION, exception.getServiceExceptionTypeEnum());

		CompletedTask completedTask5 = CompletedTask.builder()
			.userid(this.user.getId())
			.taskid(Long.MAX_VALUE)
			.build();

		exception = Assert.assertThrows(ServiceException.class, () -> completedTasksService.saveCompletedTask(completedTask5));
		Assert.assertEquals(ServiceExceptionType.CONSTRAINT_VIOLATION, exception.getServiceExceptionTypeEnum());
	}

	@Test
	public void getCompletedTaskByIdTest()
	{
		CompletedTask completedTask = CompletedTask.builder()
			.userid(this.user.getId())
			.taskid(this.task.getId())
			.build();

		completedTasksService.saveCompletedTask(completedTask);
		Assert.assertEquals(completedTask, completedTasksService.getCompletedTaskById(completedTask.getId()));
	}

	@Test
	public void getCompletedTaskByObjectTest()
	{
		long oldUserId = this.user.getId();
		long oldTaskId = this.task.getId();
		this.user.setId(null);
		this.task.setId(null);
		userRepository.save(user);
		taskRepository.save(task);

		CompletedTask completedTask1 = CompletedTask.builder()
			.userid(this.user.getId())
			.taskid(this.task.getId())
			.build();

		CompletedTask completedTask2 = CompletedTask.builder()
			.userid(oldUserId)
			.taskid(this.task.getId())
			.build();

		CompletedTask completedTask3 = CompletedTask.builder()
			.userid(this.user.getId())
			.taskid(oldTaskId)
			.build();

		CompletedTask completedTask4 = CompletedTask.builder()
			.userid(oldUserId)
			.taskid(oldTaskId)
			.build();

		completedTasksService.saveCompletedTask(completedTask1);
		completedTasksService.saveCompletedTask(completedTask2);
		completedTasksService.saveCompletedTask(completedTask3);
		completedTasksService.saveCompletedTask(completedTask4);

		Spliterator<CompletedTask> completedTaskSpliterator = completedTasksService.getByCompletedTasksObject(CompletedTask.builder()
			.id(completedTask1.getId())
			.build()
		).spliterator();
		Assert.assertTrue(StreamSupport.stream(completedTaskSpliterator, false).allMatch(completedTask1::equals));

		completedTaskSpliterator = completedTasksService.getByCompletedTasksObject(CompletedTask.builder()
			.taskid(this.task.getId())
			.build()
		).spliterator();
		StreamSupport.stream(completedTaskSpliterator, false).forEach(System.out::println);
		Assert.assertTrue(StreamSupport.stream(completedTaskSpliterator, false).allMatch(completedTask -> completedTask1.equals(completedTask) || completedTask2.equals(completedTask)));

		completedTaskSpliterator = completedTasksService.getByCompletedTasksObject(CompletedTask.builder()
			.userid(this.user.getId())
			.build()
		).spliterator();
		Assert.assertTrue(StreamSupport.stream(completedTaskSpliterator, false).allMatch(completedTask -> completedTask1.equals(completedTask) || completedTask3.equals(completedTask)));

		completedTaskSpliterator = completedTasksService.getByCompletedTasksObject(null).spliterator();
		Assert.assertEquals(4, StreamSupport.stream(completedTaskSpliterator, false).count());
	}

	@Test
	public void getAllCompletedTasksTest()
	{
		CompletedTask completedTask = CompletedTask.builder()
			.userid(this.user.getId())
			.taskid(this.task.getId())
			.build();
		completedTasksService.saveCompletedTask(completedTask);

		completedTask.setId(null);
		completedTasksService.saveCompletedTask(completedTask);

		Assert.assertEquals(2, StreamSupport.stream(completedTasksService.getAllCompletedTasks().spliterator(), false).count());
	}

	@Test
	public void updateCompletedTaskTest()
	{
		long oldUserId = this.user.getId();
		long oldTaskId = this.task.getId();
		this.user.setId(null);
		this.task.setId(null);
		userRepository.save(user);
		taskRepository.save(task);

		CompletedTask completedTask = CompletedTask.builder()
			.userid(this.user.getId())
			.taskid(this.task.getId())
			.build();
		completedTasksService.saveCompletedTask(completedTask);
		Assert.assertEquals(completedTask, completedTasksService.getCompletedTaskById(completedTask.getId()));

		CompletedTask updateCompletedTaskProperties = CompletedTask.builder()
			.id(completedTask.getId())
			.userid(oldUserId)
			.build();
		completedTasksService.updateCompletedTask(updateCompletedTaskProperties);
		Assert.assertEquals(updateCompletedTaskProperties.getUserid(), completedTasksService.getCompletedTaskById(completedTask.getId()).getUserid());

		updateCompletedTaskProperties = CompletedTask.builder()
			.id(completedTask.getId())
			.taskid(oldTaskId)
			.build();
		completedTasksService.updateCompletedTask(updateCompletedTaskProperties);
		Assert.assertEquals(updateCompletedTaskProperties.getTaskid(), completedTasksService.getCompletedTaskById(completedTask.getId()).getTaskid());
	}

	@Test
	public void updateCompletedTaskWithoutValidIdTest()
	{
		CompletedTask completedTask1 = CompletedTask.builder()
			.userid(this.user.getId())
			.taskid(this.task.getId())
			.build();

		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> completedTasksService.updateCompletedTask(completedTask1));
		Assert.assertEquals(ServiceExceptionType.ILLEGAL_ID_ARGUMENT, exception.getServiceExceptionTypeEnum());

		CompletedTask completedTask2 = CompletedTask.builder()
			.id(Long.MAX_VALUE)
			.userid(this.user.getId())
			.taskid(this.task.getId())
			.build();

		exception = Assert.assertThrows(ServiceException.class, () -> completedTasksService.updateCompletedTask(completedTask2));
		Assert.assertEquals(ServiceExceptionType.ILLEGAL_ID_ARGUMENT, exception.getServiceExceptionTypeEnum());
	}

	@Test
	public void deleteCompletedTaskTest()
	{
		CompletedTask completedTask = CompletedTask.builder()
			.userid(this.user.getId())
			.taskid(this.task.getId())
			.build();

		completedTasksService.saveCompletedTask(completedTask);
		Assert.assertEquals(completedTask, completedTasksService.getCompletedTaskById(completedTask.getId()));

		completedTasksService.deleteCompletedTask(completedTask.getId());
		Assert.assertNull(completedTasksService.getCompletedTaskById(completedTask.getId()));
	}

	@Test
	public void deleteAllCompletedTasksTest()
	{
		CompletedTask completedTask = CompletedTask.builder()
			.userid(this.user.getId())
			.taskid(this.task.getId())
			.build();
		completedTasksService.saveCompletedTask(completedTask);

		long oldCompletedTaskId = completedTask.getId();
		completedTask.setId(null);
		completedTasksService.saveCompletedTask(completedTask);

		Assert.assertNotNull(completedTasksService.getCompletedTaskById(completedTask.getId()));
		Assert.assertNotNull(completedTasksService.getCompletedTaskById(oldCompletedTaskId));

		completedTasksService.deleteAll();

		Assert.assertNull(completedTasksService.getCompletedTaskById(completedTask.getId()));
		Assert.assertNull(completedTasksService.getCompletedTaskById(oldCompletedTaskId));
	}
}
