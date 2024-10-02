package org.example.service;

import org.example.AbstractTest;
import org.example.model.CompletedTask;
import org.example.model.KeywordsForTasks;
import org.example.model.Task;
import org.example.model.User;
import org.example.repository.CompletedTasksRepository;
import org.example.repository.KeywordsForTasksRepository;
import org.example.utils.UserStatusEnum;
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

public class TaskServiceImplTest extends AbstractTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private CompletedTasksRepository completedTasksRepository;

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

    @Before
    public void setUp() {
        userService.saveUser(user);
    }

    @After
    public void tearDown() {
        taskService.deleteAll();
        userService.deleteAll();
        completedTasksRepository.deleteAll();
        keywordsForTasksRepository.deleteAll();
    }

    @Test
    public void saveValidTaskTest() {
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
    public void saveInvalidTaskTest() {
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
        Assert.assertEquals(ServiceExceptionType.ILLEGAL_ID_ARGUMENT, exception.getServiceExceptionTypeEnum());

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
    public void getTaskByObjectTest() {
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

        Iterable<Task> taskIterable = taskService.getByTasksObject(Task.builder()
                .id(task1.getId())
                .build()
        );
        Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task1::equals));

        taskIterable = taskService.getByTasksObject(Task.builder()
                .name("Example Task")
                .build()
        );
        Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task -> task2.equals(task) || task4.equals(task)));

        taskIterable = taskService.getByTasksObject(Task.builder()
                .description("Leiras")
                .build()
        );
        Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task -> task1.equals(task) || task3.equals(task)));

        taskIterable = taskService.getByTasksObject(Task.builder()
                .timeofcreation(LocalDate.now())
                .build()
        );
        Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task -> task1.equals(task) || task4.equals(task)));

        taskIterable = taskService.getByTasksObject(Task.builder()
                .ownerid(this.user.getId())
                .build()
        );
        Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task -> task3.equals(task) || task4.equals(task)));

        taskIterable = taskService.getByTasksObject(Task.builder()
                .maintaskid(task1.getId())
                .build()
        );
        Assert.assertTrue(StreamSupport.stream(taskIterable.spliterator(), false).allMatch(task3::equals));

        taskIterable = taskService.getByTasksObject(null);
        Assert.assertEquals(4, StreamSupport.stream(taskIterable.spliterator(), false).count());
    }

    @Test
    public void getTaskByNullablePropertyTest() {
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

        Iterable<Task> taskIterable = taskService.getByTasksObject(Task.builder()
                .maintaskid(null)
                .build()
        );

        Iterator<Task> taskIterator = taskIterable.iterator();

        Assert.assertEquals(2, StreamSupport.stream(taskIterable.spliterator(), false).count());
        Assert.assertEquals(task1, taskIterator.next());
        Assert.assertEquals(task2, taskIterator.next());
    }

    @Test
    public void getByFilterTest() {
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

        CompletedTask completedTask1 = CompletedTask.builder().taskid(task1.getId()).userid(this.user.getId()).build();
        CompletedTask completedTask2 = CompletedTask.builder().taskid(task1.getId()).userid(oldUserId).build();
        CompletedTask completedTask3 = CompletedTask.builder().taskid(task2.getId()).userid(this.user.getId()).build();
        CompletedTask completedTask4 = CompletedTask.builder().taskid(task3.getId()).userid(oldUserId).build();
        CompletedTask completedTask5 = CompletedTask.builder().taskid(task4.getId()).userid(this.user.getId()).build();
        CompletedTask completedTask6 = CompletedTask.builder().taskid(task4.getId()).userid(oldUserId).build();

        completedTasksRepository.saveAll(List.of(completedTask1, completedTask2, completedTask3, completedTask4, completedTask5, completedTask6));

        Filter filter = Filter.builder().name("Example Task").build();

        Spliterator<Task> taskSpliterator = taskService.getTasksByFilter(filter).spliterator();
        Assert.assertTrue(StreamSupport.stream(taskSpliterator, false).allMatch(task -> task2.equals(task) || task4.equals(task)));

        filter.setName(null);
        filter.setKeywords(List.of("ABC", "GHI"));
        taskSpliterator = taskService.getTasksByFilter(filter).spliterator();
        Assert.assertTrue(StreamSupport.stream(taskSpliterator, false).allMatch(task -> task1.equals(task) || task2.equals(task) || task3.equals(task)));

        filter.setKeywords(null);
        filter.setCompletedUserId(this.user.getId());
        taskSpliterator = taskService.getTasksByFilter(filter).spliterator();
        Assert.assertTrue(StreamSupport.stream(taskSpliterator, false).allMatch(task -> task1.equals(task) || task2.equals(task) || task4.equals(task)));

        filter.setCompletedUserId(null);
        filter.setOwnerId(this.user.getId());
        taskSpliterator = taskService.getTasksByFilter(filter).spliterator();
        Assert.assertTrue(StreamSupport.stream(taskSpliterator, false).allMatch(task -> task3.equals(task) || task4.equals(task)));

        filter.setName("Example Task");
        taskSpliterator = taskService.getTasksByFilter(filter).spliterator();
        Assert.assertTrue(StreamSupport.stream(taskSpliterator, false).allMatch(task4::equals));
    }

    @Test
    public void getByNullFilterTest() {
        ServiceException exception = Assert.assertThrows(ServiceException.class, () -> taskService.getTasksByFilter(null));
        Assert.assertEquals(ServiceExceptionType.NULL_ARGUMENT, exception.getServiceExceptionTypeEnum());
    }

    @Test
    public void getAllTasksTest() {
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
    public void updateTaskTest() {
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
        userService.saveUser(user);
        updateTaskProperties.setOwnerid(this.user.getId());
        taskService.updateTask(updateTaskProperties);
        Assert.assertEquals(updateTaskProperties.getOwnerid(), taskService.getTaskById(task.getId()).getOwnerid());
    }

    @Test
    public void updateTaskWithoutIdTest() {
        Task task = Task.builder()
                .name("Pelda Task")
                .description("Leiras")
                .timeofcreation(LocalDate.now())
                .ownerid(this.user.getId())
                .build();

        ServiceException exception = Assert.assertThrows(ServiceException.class, () -> taskService.updateTask(task));
        Assert.assertEquals(ServiceExceptionType.ILLEGAL_ID_ARGUMENT, exception.getServiceExceptionTypeEnum());
    }

    @Test
    public void updateTaskWithoutValidIdTest() {
        Task task = Task.builder()
                .id(Long.MAX_VALUE)
                .name("Pelda Task")
                .description("Leiras")
                .timeofcreation(LocalDate.now())
                .ownerid(this.user.getId())
                .build();

        ServiceException exception = Assert.assertThrows(ServiceException.class, () -> taskService.updateTask(task));
        Assert.assertEquals(ServiceExceptionType.ILLEGAL_ID_ARGUMENT, exception.getServiceExceptionTypeEnum());
    }

    @Test
    public void updateNullableValueToNullFromNotNullValueTest() {
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

        taskService.updateTask(Task.builder().id(task2.getId()).maintaskid(null).build());

        Assert.assertNull(taskService.getTaskById(task2.getId()).getMaintaskid());
    }

    @Test
    public void deleteTaskTest() {
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
