package org.example.controller;

import org.example.AbstractTest;
import org.example.model.Submission;
import org.example.model.Task;
import org.example.model.User;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.service.SubmissionService;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubmissionControllerTest extends AbstractTest {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private final String baseEndpoint = "/api/submission/";

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

    private final Submission submission1 = Submission.builder()
            .taskid(null)
            .description("Good description")
            .timeofsubmission(LocalDate.EPOCH)
            .acceptance(false)
            .submitterid(null)
            .build();

    private final Submission submission2 = Submission.builder()
            .taskid(null)
            .description("Cool description")
            .timeofsubmission(LocalDate.now())
            .acceptance(true)
            .submitterid(null)
            .build();

    private final Submission submission3 = Submission.builder()
            .taskid(null)
            .description("Good description")
            .timeofsubmission(LocalDate.now())
            .acceptance(true)
            .submitterid(null)
            .build();

    private final Submission submission4 = Submission.builder()
            .taskid(null)
            .description("Cool description")
            .timeofsubmission(LocalDate.EPOCH)
            .acceptance(null)
            .submitterid(null)
            .build();

    @Before
    public void setup() {
        this.userRepository.save(user1);
        this.userRepository.save(user2);

        this.task1.setOwnerid(user1.getId());
        this.task2.setOwnerid(user2.getId());

        this.taskRepository.save(task1);
        this.taskRepository.save(task2);

        this.submission1.setTaskid(this.task1.getId());
        this.submission1.setSubmitterid(this.user1.getId());
        this.submission2.setTaskid(this.task2.getId());
        this.submission2.setSubmitterid(this.user1.getId());
        this.submission3.setTaskid(this.task1.getId());
        this.submission3.setSubmitterid(this.user2.getId());
        this.submission4.setTaskid(this.task2.getId());
        this.submission4.setSubmitterid(this.user2.getId());

        this.submissionService.saveSubmission(submission1);
        this.submissionService.saveSubmission(submission2);
        this.submissionService.saveSubmission(submission3);
        this.submissionService.saveSubmission(submission4);
    }

    @After
    public void teardown() {
        this.submissionService.deleteAll();
        this.taskRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    public void addSubmissionTest() {
        Submission testSubmission = Submission.builder()
                .taskid(this.task1.getId())
                .description("buena descripción")
                .timeofsubmission(LocalDate.now())
                .acceptance(true)
                .submitterid(this.user1.getId())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Submission> request = new HttpEntity<>(testSubmission, headers);
        ResponseEntity<Submission> response = this.restTemplate.postForEntity(baseEndpoint, request, Submission.class);
        Submission submissionFromResponse = response.getBody();

        Submission[] submissionsFromDb = this.restTemplate.getForObject(baseEndpoint, Submission[].class);

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertNotNull(submissionFromResponse);
        Assert.assertNotNull(submissionsFromDb);
        Assert.assertEquals(submissionFromResponse, submissionsFromDb[4]);
        Assert.assertEquals(testSubmission.getTaskid(), submissionsFromDb[4].getTaskid());
        Assert.assertEquals(testSubmission.getDescription(), submissionsFromDb[4].getDescription());
        Assert.assertEquals(testSubmission.getTimeofsubmission(), submissionsFromDb[4].getTimeofsubmission());
        Assert.assertEquals(testSubmission.getAcceptance(), submissionsFromDb[4].getAcceptance());
        Assert.assertEquals(testSubmission.getSubmitterid(), submissionsFromDb[4].getSubmitterid());
    }

    @Test
    public void getSingleSubmissionTest() {
        Submission querySubmission = Submission.builder()
                .id(this.submission1.getId())
                .build();

        String paramsURI = "?id={id}" +
                "&taskid={taskid}" +
                "&description={description}" +
                "&timeofsubmission={timeofsubmission}" +
                "&acceptance={acceptance}" +
                "&submitterid={submitterid}";

        ResponseEntity<Submission[]> response = this.restTemplate.getForEntity(baseEndpoint + paramsURI,
                Submission[].class,
                querySubmission.getId(),
                querySubmission.getTaskid(),
                querySubmission.getDescription(),
                querySubmission.getTimeofsubmission(),
                querySubmission.getAcceptance(),
                querySubmission.getSubmitterid()
        );

        Submission[] submissionsAccordingToQuery = response.getBody();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(submissionsAccordingToQuery);
        Assert.assertEquals(1, submissionsAccordingToQuery.length);
        Assert.assertNotNull(submissionsAccordingToQuery[0]);
    }

    @Test
    public void getMultipleSubmissionsTest() {
        Submission querySubmission = Submission.builder()
                .acceptance(true)
                .build();

        String paramsURI = "?acceptance={acceptance}";

        ResponseEntity<Submission[]> response = this.restTemplate.getForEntity(baseEndpoint + paramsURI,
                Submission[].class,
                querySubmission.getAcceptance()
        );

        Submission[] submissionsAccordingToQuery = response.getBody();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(submissionsAccordingToQuery);
        Assert.assertEquals(2, submissionsAccordingToQuery.length);
        Assert.assertEquals(this.submission2, submissionsAccordingToQuery[0]);
        Assert.assertEquals(this.submission3, submissionsAccordingToQuery[1]);
    }

    @Test
    public void getAllSubmissionsTest() {
        ResponseEntity<Submission[]> response = this.restTemplate.getForEntity(baseEndpoint, Submission[].class);

        Submission[] submissionsInDb = response.getBody();

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(submissionsInDb);
        Assert.assertEquals(4, submissionsInDb.length);
        Assert.assertEquals(this.submission1, submissionsInDb[0]);
        Assert.assertEquals(this.submission2, submissionsInDb[1]);
        Assert.assertEquals(this.submission3, submissionsInDb[2]);
        Assert.assertEquals(this.submission4, submissionsInDb[3]);
    }

    @Test
    public void updateSubmissionTest() {
        Submission propertiesToUpdate = Submission.builder()
                .id(this.submission1.getId())
                .taskid(this.task2.getId())
                .description("buena descripción")
                .timeofsubmission(LocalDate.now())
                .acceptance(true)
                .submitterid(this.user2.getId())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Submission> request = new HttpEntity<>(propertiesToUpdate, headers);
        ResponseEntity<Submission> response = this.restTemplate.exchange(baseEndpoint, HttpMethod.PUT, request, Submission.class);
        Submission submissionFromResponse = response.getBody();

        Submission[] submissionsFromDb = this.restTemplate.getForObject(baseEndpoint, Submission[].class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertNotNull(submissionFromResponse);
        Assert.assertNotNull(submissionsFromDb);
        Assert.assertEquals(submissionFromResponse, submissionsFromDb[0]);
        Assert.assertEquals(propertiesToUpdate.getTaskid(), submissionFromResponse.getTaskid());
        Assert.assertEquals(propertiesToUpdate.getDescription(), submissionFromResponse.getDescription());
        Assert.assertEquals(propertiesToUpdate.getTimeofsubmission(), submissionFromResponse.getTimeofsubmission());
        Assert.assertEquals(propertiesToUpdate.getAcceptance(), submissionFromResponse.getAcceptance());
        Assert.assertEquals(propertiesToUpdate.getSubmitterid(), submissionFromResponse.getSubmitterid());
    }

    @Test
    public void deleteSubmissionTest() {
        ResponseEntity<Void> response = this.restTemplate.exchange(baseEndpoint + this.submission1.getId(), HttpMethod.DELETE, null, Void.class);

        ResponseEntity<Submission[]> submissionsFromDb = this.restTemplate.getForEntity(baseEndpoint, Submission[].class);
        Submission[] submissionsAccordingToQuery = submissionsFromDb.getBody();

        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Assert.assertNotNull(submissionsAccordingToQuery);
        Assert.assertEquals(3, submissionsAccordingToQuery.length);
        Assert.assertEquals(this.submission2, submissionsAccordingToQuery[0]);
        Assert.assertEquals(this.submission3, submissionsAccordingToQuery[1]);
        Assert.assertEquals(this.submission4, submissionsAccordingToQuery[2]);
    }
}
