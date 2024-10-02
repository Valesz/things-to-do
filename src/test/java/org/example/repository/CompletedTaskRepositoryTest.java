package org.example.repository;

import org.example.AbstractTest;
import org.example.model.CompletedTask;
import org.example.model.Task;
import org.example.model.User;
import org.example.utils.UserStatusEnum;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;


public class CompletedTaskRepositoryTest extends AbstractTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CompletedTasksRepository completedTasksRepository;

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
    public void setUp() {
        userRepository.save(this.user);
        this.task.setOwnerid(user.getId());
        taskRepository.save(this.task);
    }

    @After
    public void tearDown() {
        completedTasksRepository.deleteAll();
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void addValidCompletedTaskTest() {
        CompletedTask completedTask = CompletedTask.builder()
                .taskid(this.task.getId())
                .userid(this.user.getId())
                .build();

        CompletedTask savedCompletedTask = completedTasksRepository.save(completedTask);

        Assert.assertEquals(completedTask, savedCompletedTask);
    }

    @Test
    public void addInvalidTaskIdCompletedTaskTest() {
        CompletedTask completedTask = CompletedTask.builder()
                .taskid(Long.MAX_VALUE)
                .userid(this.user.getId())
                .build();

        Assert.assertThrows(DbActionExecutionException.class, () -> completedTasksRepository.save(completedTask));
    }

    @Test
    public void addInvalidUserIdCompletedTaskTest() {
        CompletedTask completedTask = CompletedTask.builder()
                .taskid(this.task.getId())
                .userid(Long.MAX_VALUE)
                .build();

        Assert.assertThrows(DbActionExecutionException.class, () -> completedTasksRepository.save(completedTask));
    }

    @Test
    public void addCompletedTaskWithMissingDataTest() {
        CompletedTask completedTask1 = CompletedTask.builder()
                .taskid(this.task.getId())
                .build();

        CompletedTask completedTask2 = CompletedTask.builder()
                .build();

        CompletedTask completedTask3 = CompletedTask.builder()
                .userid(this.user.getId())
                .build();

        Assert.assertThrows(DbActionExecutionException.class, () -> completedTasksRepository.save(completedTask1));
        Assert.assertThrows(DbActionExecutionException.class, () -> completedTasksRepository.save(completedTask2));
        Assert.assertThrows(DbActionExecutionException.class, () -> completedTasksRepository.save(completedTask3));
    }

    @Test
    public void addEmptyCompletedTaskTest() {
        CompletedTask completedTask = new CompletedTask();

        Assert.assertThrows(DbActionExecutionException.class, () -> completedTasksRepository.save(completedTask));
    }

    @Test
    public void getCompletedTaskFromDbTest() {
        CompletedTask completedTask = CompletedTask.builder()
                .taskid(this.task.getId())
                .userid(this.user.getId())
                .build();

        completedTasksRepository.save(completedTask);

        Assert.assertEquals(completedTask, completedTasksRepository.findById(completedTask.getId()).orElse(null));
    }

    @Test
    public void getMultipleCompletedTasksFromDbTest() {
        CompletedTask completedTask1 = CompletedTask.builder()
                .taskid(this.task.getId())
                .userid(this.user.getId())
                .build();

        CompletedTask completedTask2 = CompletedTask.builder()
                .taskid(this.task.getId())
                .userid(this.user.getId())
                .build();

        completedTasksRepository.save(completedTask1);
        completedTasksRepository.save(completedTask2);

        List<CompletedTask> completedTaskList = StreamSupport.stream(completedTasksRepository.findAll().spliterator(), false).toList();

        Assert.assertEquals(2, completedTaskList.size());
    }

    @Test
    public void getNonExistingCompletedTaskTest() {
        Assert.assertNull(completedTasksRepository.findById(Long.MAX_VALUE).orElse(null));
        Assert.assertNull(completedTasksRepository.findById(-1L).orElse(null));
        Assert.assertNull(completedTasksRepository.findById(1L).orElse(null));
    }

    @Test
    public void deleteCompletedTaskTest() {
        CompletedTask completedTask = CompletedTask.builder()
                .taskid(this.task.getId())
                .userid(this.user.getId())
                .build();

        completedTasksRepository.save(completedTask);

        Assert.assertNotNull(completedTasksRepository.findById(completedTask.getId()).orElse(null));

        completedTasksRepository.delete(completedTask);

        Assert.assertNull(completedTasksRepository.findById(completedTask.getId()).orElse(null));
    }

}
