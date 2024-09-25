package org.example.repository;

import org.example.AbstractTest;
import org.example.model.Submission;
import org.example.model.Task;
import org.example.model.User;
import org.example.utils.UserStatusEnum;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

public class SubmissionRepositoryTest extends AbstractTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    private User user = User.builder()
            .username("teszt elek")
            .email("teszt@teszt.teszt")
            .timeofcreation(LocalDate.now())
            .status(UserStatusEnum.AKTIV)
            .password("teszt")
            .classification(0.5)
            .precisionofanswers(0.8)
            .build();

    private Task task = Task.builder()
            .name("Pelda Task")
            .description("Leiras")
            .timeofcreation(LocalDate.now())
            .build();

    @Before
    public void setUp() {
        userRepository.save(user);
        this.task.setOwnerid(this.user.getId());
        taskRepository.save(task);
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
        taskRepository.deleteAll();
        submissionRepository.deleteAll();
    }

    @Test
    public void addValidSubmissionTest() {
        Submission submission = Submission.builder()
                .taskid(this.task.getId())
                .description("cool description")
                .timeofsubmission(LocalDate.now())
                .acceptance(true)
                .submitterid(this.user.getId())
                .build();

        Submission savedSubmission = submissionRepository.save(submission);

        Assert.assertEquals(submission, savedSubmission);
    }

    @Test
    public void addInvalidTaskIdSubmissionTest() {
        Submission submission = Submission.builder()
                .taskid(Long.MAX_VALUE)
                .description("cool description")
                .timeofsubmission(LocalDate.now())
                .acceptance(true)
                .submitterid(this.user.getId())
                .build();

        Assert.assertThrows(DbActionExecutionException.class, () -> submissionRepository.save(submission));
    }

    @Test
    public void addInvalidSubmitterIdSubmissionTest() {
        Submission submission = Submission.builder()
                .taskid(this.task.getId())
                .description("cool description")
                .timeofsubmission(LocalDate.now())
                .acceptance(true)
                .submitterid(Long.MAX_VALUE)
                .build();

        Assert.assertThrows(DbActionExecutionException.class, () -> submissionRepository.save(submission));
    }

    @Test
    public void addEmptySubmissionTest() {
        Submission submission = new Submission();

        Assert.assertThrows(DbActionExecutionException.class, () -> submissionRepository.save(submission));
    }

    @Test
    public void getSubmissionFromDbTest() {
        Submission submission = Submission.builder()
                .taskid(this.task.getId())
                .description("cool description")
                .timeofsubmission(LocalDate.now())
                .acceptance(true)
                .submitterid(this.user.getId())
                .build();

        submissionRepository.save(submission);

        Assert.assertEquals(submission, submissionRepository.findById(submission.getId()).orElse(null));
    }

    @Test
    public void getMultipleSubmissionsFromDbTest() {
        Submission submission1 = Submission.builder()
                .taskid(this.task.getId())
                .description("cool description")
                .timeofsubmission(LocalDate.now())
                .acceptance(true)
                .submitterid(this.user.getId())
                .build();

        Submission submission2 = Submission.builder()
                .taskid(this.task.getId())
                .description("cool description number 2")
                .timeofsubmission(LocalDate.now())
                .acceptance(true)
                .submitterid(this.user.getId())
                .build();

        submissionRepository.save(submission1);
        submissionRepository.save(submission2);

        List<Submission> submissionList = StreamSupport.stream(submissionRepository.findAll().spliterator(), false).toList();

        Assert.assertEquals(2, submissionList.size());
    }

    @Test
    public void getNonExistingSubmissionTest() {
        Assert.assertNull(submissionRepository.findById(Long.MAX_VALUE).orElse(null));
        Assert.assertNull(submissionRepository.findById(-1L).orElse(null));
        Assert.assertNull(submissionRepository.findById(1L).orElse(null));
    }

    @Test
    public void deleteSubmissionTest() {
        Submission submission = Submission.builder()
                .taskid(this.task.getId())
                .description("cool description")
                .timeofsubmission(LocalDate.now())
                .acceptance(true)
                .submitterid(this.user.getId())
                .build();

        submissionRepository.save(submission);

        Assert.assertNotNull(submissionRepository.findById(submission.getId()).orElse(null));

        submissionRepository.delete(submission);

        Assert.assertNull(submissionRepository.findById(submission.getId()).orElse(null));
    }

    @Test
    public void changeAcceptanceTest() {
        Submission submission = Submission.builder()
                .taskid(this.task.getId())
                .description("cool description number 2")
                .timeofsubmission(LocalDate.now())
                .acceptance(true)
                .submitterid(this.user.getId())
                .build();

        submissionRepository.save(submission);

        submissionRepository.setAcceptance(submission.getId(), false);

        Submission savedSubmission = submissionRepository.findById(submission.getId()).orElse(null);

        Assert.assertNotNull(savedSubmission);

        Assert.assertFalse(savedSubmission.getAcceptance());
    }

}
