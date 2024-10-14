package org.example.repository;

import org.example.AbstractTest;
import org.example.model.Task;
import org.example.model.User;
import org.example.utils.enums.UserStatusEnum;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

public class TaskRepositoryTest extends AbstractTest
{

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

	@Before
	public void setUp()
	{
		userRepository.save(user);
	}

	@After
	public void tearDown()
	{
		taskRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	public void addTaskWithValidPropertiesTest()
	{
		Task task1 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		taskRepository.save(task1);

		Task taskInDb = taskRepository.findById(task1.getId()).orElse(null);

		Assert.assertNotNull(taskInDb);
		Assert.assertEquals(task1, taskInDb);
	}

	@Test
	public void addTaskWithInvalidOwnerIdTest()
	{
		Task task1 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(Long.MAX_VALUE)
			.build();

		Assert.assertThrows(DbActionExecutionException.class, () -> taskRepository.save(task1));
	}

	@Test
	public void addTaskWithInvalidMainTaskIdTest()
	{
		Task task1 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.maintaskid(Long.MAX_VALUE)
			.ownerid(this.user.getId())
			.build();

		Assert.assertThrows(DbActionExecutionException.class, () -> taskRepository.save(task1));
	}

	@Test
	public void addTaskWithValidMainTaskIdTest()
	{
		Task task1 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		taskRepository.save(task1);

		Task task2 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.maintaskid(task1.getId())
			.ownerid(this.user.getId())
			.build();

		Task task2FromDb = taskRepository.save(task2);

		Assert.assertNotNull(task2FromDb.getMaintaskid());
	}

	@Test
	public void addEmptyTaskTest()
	{
		Task task = new Task();

		Assert.assertThrows(DbActionExecutionException.class, () -> taskRepository.save(task));
	}

	@Test
	public void getTaskFromDbTest()
	{
		Task task1 = Task.builder()
			.name("Pelda Task")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		Task task1FromDb = taskRepository.save(task1);

		Assert.assertEquals(task1, task1FromDb);
	}

	@Test
	public void getMultipleTasksFromDbTest()
	{
		Task task1 = Task.builder()
			.name("Pelda Task 1")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		taskRepository.save(task1);

		Task task2 = Task.builder()
			.name("Pelda Task 1")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.maintaskid(task1.getId())
			.ownerid(this.user.getId())
			.build();

		taskRepository.save(task2);

		List<Task> tasksFromDb = StreamSupport.stream(taskRepository.findAll().spliterator(), false).toList();

		Assert.assertEquals(2, tasksFromDb.size());
	}

	@Test
	public void getNonExistingTaskFromDbTest()
	{
		Assert.assertNull(taskRepository.findById(Long.MAX_VALUE).orElse(null));
		Assert.assertNull(taskRepository.findById(-1L).orElse(null));
		Assert.assertNull(taskRepository.findById(1L).orElse(null));
	}

	@Test
	public void deleteTaskTest()
	{
		Task task = Task.builder()
			.name("Pelda Task 1")
			.description("Leiras")
			.timeofcreation(LocalDate.now())
			.ownerid(this.user.getId())
			.build();

		taskRepository.save(task);

		Assert.assertNotNull(taskRepository.findById(task.getId()).orElse(null));

		taskRepository.delete(task);

		Assert.assertNull(taskRepository.findById(task.getId()).orElse(null));
	}
}
