package org.example.service;

import org.example.AbstractTest;
import org.example.model.Submission;
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
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

public class SubmissionServiceImplTest extends AbstractTest
{

	@Autowired
	private SubmissionService submissionService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TaskRepository taskRepository;

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
		task.setOwnerid(this.user.getId());
		taskRepository.save(task);
	}

	@After
	public void teardown()
	{
		submissionService.deleteAll();
		taskRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	public void saveValidSubmissionTest()
	{
		Submission submission = Submission.builder()
			.taskid(this.task.getId())
			.description("Cool description")
			.timeofsubmission(LocalDate.now())
			.acceptance(true)
			.submitterid(this.user.getId())
			.build();

		submissionService.saveSubmission(submission);
		Assert.assertEquals(submission, submissionService.getSubmissionById(submission.getId()));

		submission.setId(null);
		submission.setAcceptance(null);
		submissionService.saveSubmission(submission);
		Assert.assertEquals(submission, submissionService.getSubmissionById(submission.getId()));
	}

	@Test
	public void saveInvalidSubmissionTest()
	{
		Submission submission1 = Submission.builder()
			.taskid(this.task.getId())
			.description("Cool description")
			.build();

		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> submissionService.saveSubmission(submission1));
		Assert.assertEquals(ServiceExceptionType.NULL_ARGUMENT, exception.getServiceExceptionTypeEnum());

		Submission submission2 = Submission.builder()
			.timeofsubmission(LocalDate.now())
			.acceptance(true)
			.submitterid(this.user.getId())
			.build();

		exception = Assert.assertThrows(ServiceException.class, () -> submissionService.saveSubmission(submission2));
		Assert.assertEquals(ServiceExceptionType.NULL_ARGUMENT, exception.getServiceExceptionTypeEnum());

		Submission submission3 = Submission.builder()
			.taskid(Long.MAX_VALUE)
			.description("Cool description")
			.timeofsubmission(LocalDate.now())
			.acceptance(true)
			.submitterid(this.user.getId())
			.build();

		exception = Assert.assertThrows(ServiceException.class, () -> submissionService.saveSubmission(submission3));
		Assert.assertEquals(ServiceExceptionType.CONSTRAINT_VIOLATION, exception.getServiceExceptionTypeEnum());

		Submission submission4 = Submission.builder()
			.taskid(this.task.getId())
			.description("Cool description")
			.timeofsubmission(LocalDate.now())
			.acceptance(true)
			.submitterid(Long.MAX_VALUE)
			.build();

		exception = Assert.assertThrows(ServiceException.class, () -> submissionService.saveSubmission(submission4));
		Assert.assertEquals(ServiceExceptionType.CONSTRAINT_VIOLATION, exception.getServiceExceptionTypeEnum());

		Submission submission5 = Submission.builder()
			.id(1L)
			.taskid(this.task.getId())
			.description("Cool description")
			.timeofsubmission(LocalDate.now())
			.acceptance(true)
			.submitterid(this.user.getId())
			.build();

		exception = Assert.assertThrows(ServiceException.class, () -> submissionService.saveSubmission(submission5));
		Assert.assertEquals(ServiceExceptionType.ILLEGAL_ID_ARGUMENT, exception.getServiceExceptionTypeEnum());
	}

	@Test
	public void getSubmissionByIdTest()
	{
		Submission submission = Submission.builder()
			.taskid(this.task.getId())
			.description("Cool description")
			.timeofsubmission(LocalDate.now())
			.acceptance(true)
			.submitterid(this.user.getId())
			.build();

		submissionService.saveSubmission(submission);
		Assert.assertEquals(submission, submissionService.getSubmissionById(submission.getId()));
	}

	@Test
	public void getSubmissionByObjectTest()
	{
		long oldUserId = this.user.getId();
		long oldTaskId = this.task.getId();
		this.user.setId(null);
		this.task.setId(null);
		userRepository.save(user);
		taskRepository.save(task);

		Submission submission1 = Submission.builder()
			.taskid(this.task.getId())
			.description("Good description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(false)
			.submitterid(this.user.getId())
			.build();

		Submission submission2 = Submission.builder()
			.taskid(oldTaskId)
			.description("Cool description")
			.timeofsubmission(LocalDate.now())
			.acceptance(true)
			.submitterid(this.user.getId())
			.build();

		Submission submission3 = Submission.builder()
			.taskid(this.task.getId())
			.description("Good description")
			.timeofsubmission(LocalDate.now())
			.acceptance(true)
			.submitterid(oldUserId)
			.build();

		Submission submission4 = Submission.builder()
			.taskid(oldTaskId)
			.description("Cool description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(null)
			.submitterid(oldUserId)
			.build();

		submissionService.saveSubmission(submission1);
		submissionService.saveSubmission(submission2);
		submissionService.saveSubmission(submission3);
		submissionService.saveSubmission(submission4);

		Spliterator<Submission> submissionSpliterator = submissionService.getBySubmissionsObject(Submission.builder()
			.id(submission1.getId())
			.build()
		).spliterator();
		Assert.assertTrue(StreamSupport.stream(submissionSpliterator, false).allMatch(submission1::equals));

		submissionSpliterator = submissionService.getBySubmissionsObject(Submission.builder()
			.taskid(oldTaskId)
			.build()
		).spliterator();
		Assert.assertTrue(StreamSupport.stream(submissionSpliterator, false).allMatch(submission -> submission2.equals(submission) || submission4.equals(submission)));

		submissionSpliterator = submissionService.getBySubmissionsObject(Submission.builder()
			.description("Good Description")
			.build()
		).spliterator();
		Assert.assertTrue(StreamSupport.stream(submissionSpliterator, false).allMatch(submission -> submission1.equals(submission) || submission3.equals(submission)));

		submissionSpliterator = submissionService.getBySubmissionsObject(Submission.builder()
			.timeofsubmission(LocalDate.EPOCH)
			.build()
		).spliterator();
		Assert.assertTrue(StreamSupport.stream(submissionSpliterator, false).allMatch(submission -> submission1.equals(submission) || submission4.equals(submission)));

		submissionSpliterator = submissionService.getBySubmissionsObject(Submission.builder()
			.acceptance(true)
			.build()
		).spliterator();
		Assert.assertTrue(StreamSupport.stream(submissionSpliterator, false).allMatch(submission -> submission2.equals(submission) || submission3.equals(submission)));

		submissionSpliterator = submissionService.getBySubmissionsObject(Submission.builder()
			.submitterid(oldUserId)
			.build()
		).spliterator();
		Assert.assertTrue(StreamSupport.stream(submissionSpliterator, false).allMatch(submission -> submission3.equals(submission) || submission4.equals(submission)));

		submissionSpliterator = submissionService.getBySubmissionsObject(null).spliterator();
		Assert.assertEquals(4, StreamSupport.stream(submissionSpliterator, false).count());
	}

	@Test
	public void getSubmissionByNullableValueTest()
	{
		Submission submission1 = Submission.builder()
			.taskid(this.task.getId())
			.description("Cool description")
			.timeofsubmission(LocalDate.now())
			.acceptance(true)
			.submitterid(this.user.getId())
			.build();

		Submission submission2 = Submission.builder()
			.taskid(this.task.getId())
			.description("Good description")
			.timeofsubmission(LocalDate.now())
			.acceptance(true)
			.submitterid(this.user.getId())
			.build();

		Submission submission3 = Submission.builder()
			.taskid(this.task.getId())
			.description("Cool description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(null)
			.submitterid(this.user.getId())
			.build();
		submissionService.saveSubmission(submission1);
		submissionService.saveSubmission(submission2);
		submissionService.saveSubmission(submission3);

		Iterable<Submission> submissionIterable = submissionService.getBySubmissionsObject(Submission.builder()
			.acceptance(null)
			.build()
		);
		Iterator<Submission> submissionIterator = submissionIterable.iterator();

		Assert.assertEquals(1, StreamSupport.stream(submissionIterable.spliterator(), false).count());
		Assert.assertEquals(submission1, submissionIterator.next());
		Assert.assertFalse(submissionIterator.hasNext());
	}

	@Test
	public void getAllSubmissionsTest()
	{
		Submission submission1 = Submission.builder()
			.taskid(this.task.getId())
			.description("Good description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(false)
			.submitterid(this.user.getId())
			.build();
		submissionService.saveSubmission(submission1);

		submission1.setId(null);
		submissionService.saveSubmission(submission1);

		long submissionList = StreamSupport.stream(submissionService.getAllSubmissions().spliterator(), false).count();
		Assert.assertEquals(2L, submissionList);
	}

	@Test
	public void updateSubmissionTest()
	{
		long oldUserId = this.user.getId();
		long oldTaskId = this.task.getId();
		this.user.setId(null);
		this.task.setId(null);
		userRepository.save(user);
		taskRepository.save(task);

		Submission submission = Submission.builder()
			.taskid(this.task.getId())
			.description("Good description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(false)
			.submitterid(this.user.getId())
			.build();
		submissionService.saveSubmission(submission);
		Assert.assertEquals(submission, submissionService.getSubmissionById(submission.getId()));

		Submission updateSubmissionProperties = Submission.builder()
			.id(submission.getId())
			.taskid(oldTaskId)
			.build();
		submissionService.updateSubmission(updateSubmissionProperties);
		Assert.assertEquals(updateSubmissionProperties.getTaskid(), submissionService.getSubmissionById(submission.getId()).getTaskid());

		updateSubmissionProperties = Submission.builder()
			.id(submission.getId())
			.taskid(oldTaskId)
			.build();
		submissionService.updateSubmission(updateSubmissionProperties);
		Assert.assertEquals(updateSubmissionProperties.getTaskid(), submissionService.getSubmissionById(submission.getId()).getTaskid());

		updateSubmissionProperties = Submission.builder()
			.id(submission.getId())
			.description("Even better description")
			.build();
		submissionService.updateSubmission(updateSubmissionProperties);
		Assert.assertEquals(updateSubmissionProperties.getDescription(), submissionService.getSubmissionById(submission.getId()).getDescription());

		updateSubmissionProperties = Submission.builder()
			.id(submission.getId())
			.timeofsubmission(LocalDate.EPOCH)
			.build();
		submissionService.updateSubmission(updateSubmissionProperties);
		Assert.assertEquals(updateSubmissionProperties.getTimeofsubmission(), submissionService.getSubmissionById(submission.getId()).getTimeofsubmission());

		updateSubmissionProperties = Submission.builder()
			.id(submission.getId())
			.acceptance(false)
			.build();
		submissionService.updateSubmission(updateSubmissionProperties);
		Assert.assertEquals(updateSubmissionProperties.getAcceptance(), submissionService.getSubmissionById(submission.getId()).getAcceptance());

		updateSubmissionProperties = Submission.builder()
			.id(submission.getId())
			.submitterid(oldUserId)
			.build();
		submissionService.updateSubmission(updateSubmissionProperties);
		Assert.assertEquals(updateSubmissionProperties.getSubmitterid(), submissionService.getSubmissionById(submission.getId()).getSubmitterid());
	}

	@Test
	public void updateSubmissionWithoutValidIdTest()
	{
		Submission submission = Submission.builder()
			.id(Long.MAX_VALUE)
			.taskid(this.task.getId())
			.description("Good description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(false)
			.submitterid(this.user.getId())
			.build();

		ServiceException exception = Assert.assertThrows(ServiceException.class, () -> submissionService.updateSubmission(submission));
		Assert.assertEquals(ServiceExceptionType.ILLEGAL_ID_ARGUMENT, exception.getServiceExceptionTypeEnum());
	}

	@Test
	public void deleteSubmissionTest()
	{
		Submission submission = Submission.builder()
			.taskid(this.task.getId())
			.description("Good description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(false)
			.submitterid(this.user.getId())
			.build();
		submissionService.saveSubmission(submission);
		Assert.assertNotNull(submissionService.getSubmissionById(submission.getId()));

		submissionService.deleteSubmission(submission.getId());
		Assert.assertNull(submissionService.getSubmissionById(submission.getId()));
	}

	@Test
	public void deleteAllSubmissionsTest()
	{
		Submission submission = Submission.builder()
			.taskid(this.task.getId())
			.description("Good description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(false)
			.submitterid(this.user.getId())
			.build();

		submissionService.saveSubmission(submission);
		long oldSubmissionId = submission.getId();
		submission.setId(null);
		submissionService.saveSubmission(submission);

		Assert.assertNotNull(submissionService.getSubmissionById(submission.getId()));
		Assert.assertNotNull(submissionService.getSubmissionById(oldSubmissionId));

		submissionService.deleteAll();

		Assert.assertNull(submissionService.getSubmissionById(submission.getId()));
		Assert.assertNull(submissionService.getSubmissionById(oldSubmissionId));
	}

	@Test
	public void setAcceptanceTest()
	{
		Submission submission = Submission.builder()
			.taskid(this.task.getId())
			.description("Good description")
			.timeofsubmission(LocalDate.EPOCH)
			.acceptance(false)
			.submitterid(this.user.getId())
			.build();
		submissionService.saveSubmission(submission);

		submissionService.setAcceptance(submission.getId(), true);
		Assert.assertTrue(submissionService.getSubmissionById(submission.getId()).getAcceptance());

		submissionService.setAcceptance(submission.getId(), null);
		Assert.assertNull(submissionService.getSubmissionById(submission.getId()).getAcceptance());
	}
}
