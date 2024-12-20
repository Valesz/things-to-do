package org.example.service;

import org.example.AbstractTest;
import org.example.model.KeywordsForTasks;
import org.example.model.Submission;
import org.example.model.Task;
import org.example.model.listing.TaskListingFilter;
import org.example.model.User;
import org.example.repository.KeywordsForTasksRepository;
import org.example.repository.SubmissionRepository;
import org.example.utils.enums.SubmissionAcceptanceEnum;
import org.example.utils.enums.UserStatusEnum;
import org.example.utils.exceptions.ServiceException;
import org.example.utils.exceptions.ServiceExceptionType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

public class TaskServiceImplTest extends AbstractTest
{

	@Autowired
	private TaskService taskService;

	@Autowired
	private UserService userService;

	@Autowired
	private SubmissionRepository submissionRepository;

	@Autowired
	private KeywordsForTasksRepository keywordsForTasksRepository;

	private final User user = User.builder()
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
		userService.saveUser(user);
	}

	@After
	public void tearDown()
	{
		taskService.deleteAll();
		userService.deleteAll();
		submissionRepository.deleteAll();
		keywordsForTasksRepository.deleteAll();
	}

	@Test
	public void saveValidTaskTest()
	{
		Task task = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		taskService.saveTask(task);

		Task savedTask = taskService.getTaskById(task.getId());

		Assert.assertEquals(task, savedTask);
	}

	@Test
	public void saveInvalidTaskTest()
	{
		Task task1 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.build();
		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> taskService.saveTask(task1));
		Assert.assertEquals(ServiceExceptionType.NULL_ARGUMENT, exception.getServiceExceptionTypeEnum());

		Task task2 = Task.builder()
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();
		exception = Assert.assertThrows(ServiceException.class, () -> taskService.saveTask(task2));
		Assert.assertEquals(ServiceExceptionType.NULL_ARGUMENT, exception.getServiceExceptionTypeEnum());

		Task task3 = Task.builder()
			.id(1L)
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();
		exception = Assert.assertThrows(ServiceException.class, () -> taskService.saveTask(task3));
		Assert.assertEquals(ServiceExceptionType.ID_GIVEN, exception.getServiceExceptionTypeEnum());

		Task task4 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(Long.MAX_VALUE)
			.build();
		exception = Assert.assertThrows(ServiceException.class, () -> taskService.saveTask(task4));
		Assert.assertEquals(ServiceExceptionType.CONSTRAINT_VIOLATION, exception.getServiceExceptionTypeEnum());
	}

	@Test
	public void getTaskByObjectTest()
	{
		Task task1 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		Task task2 = Task.builder()
			.name("Example Task")
			.description("Cool Description")
			.timeofcreation(LocalDate.EPOCH)
			.ownerid(this.user.getId())
			.build();

		this.user.setId(null);
		this.user.setUsername("Teszt Elek2");
		this.user.setPassword("tesztA12");
		userService.saveUser(this.user);

		taskService.saveTask(task1);
		taskService.saveTask(task2);

		Task task3 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.EPOCH)
			.ownerid(this.user.getId())
			.maintaskid(task1.getId())
			.build();

		Task task4 = Task.builder()
			.name("Example Task")
			.description("Cool Description")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.maintaskid(task2.getId())
			.build();

		taskService.saveTask(task3);
		taskService.saveTask(task4);

		Iterable<TaskListingFilter> taskIterable = taskService.getTasksByFilter(TaskListingFilter.builder()
			.id(task1.getId())
			.build(), 0, 5
		);
		Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task1::listingFilterEquals));

		taskIterable = taskService.getTasksByFilter(TaskListingFilter.builder()
			.name("Example Task")
			.build(), 0, 5
		);
		Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task -> task2.listingFilterEquals(task) || task4.listingFilterEquals(task)));

		taskIterable = taskService.getTasksByFilter(TaskListingFilter.builder()
			.description("Leiras")
			.build(), 0, 5
		);
		Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task -> task1.listingFilterEquals(task) || task3.listingFilterEquals(task)));

		taskIterable = taskService.getTasksByFilter(TaskListingFilter.builder()
			.createdAfter(LocalDate.now())
			.createdBefore(LocalDate.now())
			.build(), 0, 5
		);
		Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task -> task1.listingFilterEquals(task) || task4.listingFilterEquals(task)));

		taskIterable = taskService.getTasksByFilter(TaskListingFilter.builder()
			.ownerid(this.user.getId())
			.build(), 0, 5
		);
		Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task -> task3.listingFilterEquals(task) || task4.listingFilterEquals(task)));

		taskIterable = taskService.getTasksByFilter(TaskListingFilter.builder()
			.maintaskid(task1.getId())
			.build(), 0, 5
		);
		Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task3::listingFilterEquals));

		taskIterable = taskService.getTasksByFilter(null, 0, 5);
		Assert.assertEquals(4, StreamSupport.stream(taskIterable.spliterator(), false).count());
	}

	@Test
	public void getTaskByNullablePropertyTest()
	{
		Task task1 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.maintaskid(null)
			.build();

		Task task2 = Task.builder()
			.name("Example Task")
			.description("Cool Description")
			.timeofcreation(LocalDate.EPOCH)
			.ownerid(this.user.getId())
			.maintaskid(null)
			.build();

		this.user.setId(null);
		this.user.setUsername("test");
		this.user.setPassword("tesztA12");
		userService.saveUser(this.user);

		taskService.saveTask(task1);
		taskService.saveTask(task2);

		Task task3 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.EPOCH)
			.ownerid(this.user.getId())
			.maintaskid(task1.getId())
			.build();

		taskService.saveTask(task3);

		Iterable<TaskListingFilter> taskIterable = taskService.getTasksByFilter(TaskListingFilter.builder()
			.maintaskid(0L)
			.build(), 0, 5
		);

		Iterator<TaskListingFilter> taskIterator = taskIterable.iterator();

		Assert.assertEquals(2, StreamSupport.stream(taskIterable.spliterator(), false).count());
		Assert.assertTrue(task1.listingFilterEquals(taskIterator.next()));
		Assert.assertTrue(task2.listingFilterEquals(taskIterator.next()));
	}

	@Test
	public void getByFilterTest()
	{
		long oldUserId = this.user.getId();

		Task task1 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		Task task2 = Task.builder()
			.name("Example Task")
			.description("Cool Description")
			.timeofcreation(LocalDate.EPOCH)
			.ownerid(this.user.getId())
			.build();

		this.user.setId(null);
		this.user.setUsername(this.user.getUsername() + "2");
		this.user.setPassword("tesztA12");
		userService.saveUser(this.user);

		Task task3 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.EPOCH)
			.ownerid(this.user.getId())
			.build();

		Task task4 = Task.builder()
			.name("Example Task")
			.description("Cool Description")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		taskService.saveTask(task1);
		taskService.saveTask(task2);
		taskService.saveTask(task3);
		taskService.saveTask(task4);

		KeywordsForTasks keyword1 = KeywordsForTasks.builder().taskid(task1.getId()).keyword("ABC").build();
		KeywordsForTasks keyword2 = KeywordsForTasks.builder().taskid(task1.getId()).keyword("DEF").build();
		KeywordsForTasks keyword3 = KeywordsForTasks.builder().taskid(task1.getId()).keyword("GHI").build();
		KeywordsForTasks keyword4 = KeywordsForTasks.builder().taskid(task2.getId()).keyword("ABC").build();
		KeywordsForTasks keyword5 = KeywordsForTasks.builder().taskid(task2.getId()).keyword("DEF").build();
		KeywordsForTasks keyword6 = KeywordsForTasks.builder().taskid(task3.getId()).keyword("GHI").build();

		keywordsForTasksRepository.saveAll(List.of(keyword1, keyword2, keyword3, keyword4, keyword5, keyword6));

		Submission submission1 = Submission.builder()
			.taskid(task1.getId())
			.description("Good description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(SubmissionAcceptanceEnum.REJECTED)
			.submitterid(this.user.getId())
			.build();

		Submission submission2 = Submission.builder()
			.taskid(task1.getId())
			.description("Cool description")
			.timeofsubmission(LocalDate.now())
			.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
			.submitterid(oldUserId)
			.build();

		Submission submission3 = Submission.builder()
			.taskid(task2.getId())
			.description("Good description")
			.timeofsubmission(LocalDate.now())
			.acceptance(SubmissionAcceptanceEnum.ACCEPTED)
			.submitterid(this.user.getId())
			.build();

		Submission submission4 = Submission.builder()
			.taskid(task3.getId())
			.description("Cool description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(SubmissionAcceptanceEnum.IN_PROGRESS)
			.submitterid(oldUserId)
			.build();

		Submission submission5 = Submission.builder()
			.taskid(task4.getId())
			.description("Cool description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(SubmissionAcceptanceEnum.IN_PROGRESS)
			.submitterid(this.user.getId())
			.build();

		Submission submission6 = Submission.builder()
			.taskid(task4.getId())
			.description("Cool description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(SubmissionAcceptanceEnum.IN_PROGRESS)
			.submitterid(oldUserId)
			.build();

		submissionRepository.saveAll(List.of(submission1, submission2, submission3, submission4, submission5, submission6));

		TaskListingFilter filter = TaskListingFilter.builder().name("Example Task").build();

		Spliterator<TaskListingFilter> taskSpliterator = taskService.getTasksByFilter(filter, 0, 5).spliterator();
		Assert.assertTrue(StreamSupport.stream(taskSpliterator, false).allMatch(task -> task2.listingFilterEquals(task) || task4.listingFilterEquals(task)));

		filter.setName(null);
		filter.setKeywords(List.of("ABC", "GHI"));
		taskSpliterator = taskService.getTasksByFilter(filter, 0, 5).spliterator();
		Assert.assertTrue(StreamSupport.stream(taskSpliterator, false).allMatch(task -> task1.listingFilterEquals(task) || task2.listingFilterEquals(task) || task3.listingFilterEquals(task)));

		filter.setKeywords(null);
		filter.setCompletedUserId(this.user.getId());
		filter.setCompleted(true);
		taskSpliterator = taskService.getTasksByFilter(filter, 0, 5).spliterator();
		Assert.assertTrue(StreamSupport.stream(taskSpliterator, false).allMatch(task -> task1.listingFilterEquals(task) || task2.listingFilterEquals(task) || task4.listingFilterEquals(task)));

		filter.setCompletedUserId(null);
		filter.setOwnerid(this.user.getId());
		taskSpliterator = taskService.getTasksByFilter(filter, 0, 5).spliterator();
		Assert.assertTrue(StreamSupport.stream(taskSpliterator, false).allMatch(task -> task3.listingFilterEquals(task) || task4.listingFilterEquals(task)));

		filter.setName("Example Task");
		taskSpliterator = taskService.getTasksByFilter(filter, 0, 5).spliterator();
		Assert.assertTrue(StreamSupport.stream(taskSpliterator, false).allMatch(task4::listingFilterEquals));
	}

	@Test
	public void getAllTasksTest()
	{
		Task task1 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		Task task2 = Task.builder()
			.name("Example Task")
			.description("Cool Description")
			.timeofcreation(LocalDate.EPOCH)
			.ownerid(this.user.getId())
			.build();

		taskService.saveTask(task1);
		taskService.saveTask(task2);

		Assert.assertEquals(2, StreamSupport.stream(taskService.getAllTasks().spliterator(), false).count());
	}

	@Test
	public void updateTaskTest()
	{
		Task task = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		taskService.saveTask(task);
		Assert.assertEquals(task, taskService.getTaskById(task.getId()));

		Task updateTaskProperties = Task.builder()
			.id(task.getId())
			.name("Example Task")
			.build();
		taskService.updateTask(updateTaskProperties);
		Assert.assertEquals(updateTaskProperties.getName(), taskService.getTaskById(task.getId()).getName());

		updateTaskProperties.setDescription("Cool Description");
		taskService.updateTask(updateTaskProperties);
		Assert.assertEquals(updateTaskProperties.getDescription(), taskService.getTaskById(task.getId()).getDescription());

		updateTaskProperties.setTimeofcreation(LocalDate.EPOCH);
		taskService.updateTask(updateTaskProperties);
		Assert.assertEquals(updateTaskProperties.getTimeofcreation(), taskService.getTaskById(task.getId()).getTimeofcreation());

		updateTaskProperties.setMaintaskid(task.getId());
		taskService.updateTask(updateTaskProperties);
		Assert.assertEquals(updateTaskProperties.getMaintaskid(), taskService.getTaskById(task.getId()).getMaintaskid());

		this.user.setId(null);
		this.user.setUsername(this.user.getUsername() + "2");
		this.user.setPassword("tesztA12");
		userService.saveUser(user);
		updateTaskProperties.setOwnerid(this.user.getId());
		taskService.updateTask(updateTaskProperties);
		Assert.assertEquals(updateTaskProperties.getOwnerid(), taskService.getTaskById(task.getId()).getOwnerid());
	}

	@Test
	public void updateTaskWithoutIdTest()
	{
		Task task = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> taskService.updateTask(task));
		Assert.assertEquals(ServiceExceptionType.ID_NOT_GIVEN, exception.getServiceExceptionTypeEnum());
	}

	@Test
	public void updateTaskWithoutValidIdTest()
	{
		Task task = Task.builder()
			.id(Long.MAX_VALUE)
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> taskService.updateTask(task));
		Assert.assertEquals(ServiceExceptionType.ID_NOT_FOUND, exception.getServiceExceptionTypeEnum());
	}

	@Test
	public void updateNullableValueToNullFromNotNullValueTest()
	{
		Task task1 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();
		taskService.saveTask(task1);

		Task task2 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.maintaskid(task1.getId())
			.ownerid(this.user.getId())
			.build();
		taskService.saveTask(task2);

		taskService.setMainTaskId(task2.getId(), null);

		Assert.assertNull(taskService.getTaskById(task2.getId()).getMaintaskid());
	}

	@Test
	public void deleteTaskTest()
	{
		Task task = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		taskService.saveTask(task);
		Assert.assertEquals(task, taskService.getTaskById(task.getId()));

		taskService.deleteTask(task.getId());
		Assert.assertNull(taskService.getTaskById(task.getId()));
	}
}
