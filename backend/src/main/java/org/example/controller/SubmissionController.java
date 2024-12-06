package org.example.controller;

import java.util.Objects;
import org.example.MyConfiguration;
import org.example.model.Submission;
import org.example.model.User;
import org.example.model.listing.SubmissionListing;
import org.example.service.SubmissionService;
import org.example.service.UserService;
import org.example.utils.enums.SubmissionAcceptanceEnum;
import org.example.utils.exceptions.ServiceException;
import org.example.utils.exceptions.ServiceExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/submission")
@Import(value = {MyConfiguration.class})
public class SubmissionController
{

	@Autowired
	SubmissionService submissionService;

	@Autowired
	UserService userService;

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Submission addSubmission(@RequestBody Submission submission,
		@RequestHeader(name = "Authorization") String token)
	{
		try
		{
			User senderByToken = userService.getUserByToken(token.substring(7));

			User senderByObject = null;

			if (submission.getSubmitterid() != null)
			{
				senderByObject = userService.getUserById(submission.getSubmitterid());
			}

			if (senderByObject != null && senderByToken != null && !Objects.equals(senderByToken.getId(), senderByObject.getId()))
			{
				throw new ServiceException(ServiceExceptionType.UNAUTHORIZED_TO_CHANGE,
					"Creating tasks in the name of other's are not allowed!"
				);
			}

			return submissionService.saveSubmission(submission);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case NULL_ARGUMENT, ID_GIVEN, INVALID_ARGUMENT:
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

				case CONSTRAINT_VIOLATION:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@RequestMapping(value = "/", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public Submission updateSubmission(@RequestBody Submission submission,
		@RequestHeader(name = "Authorization") String token)
	{
		try
		{
			User user = userService.getUserByToken(token.substring(7));

			Submission submissionInDatabase = null;
			if (submission.getId() != null)
			{
				submissionInDatabase = submissionService.getSubmissionById(submission.getId());
			}

			if (submissionInDatabase != null
				&& !Objects.equals(user.getId(), submissionInDatabase.getSubmitterid()))
			{
				throw new ServiceException(ServiceExceptionType.UNAUTHORIZED_TO_CHANGE,
					"Changing others submission data is not allowed!"
				);
			}

			return submissionService.updateSubmission(submission);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case UNAUTHORIZED_TO_CHANGE:
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());

				case ID_NOT_GIVEN, INVALID_ARGUMENT:
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

				case ID_NOT_FOUND, CONSTRAINT_VIOLATION:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@RequestMapping(value = "/acceptance", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public Integer updateAcceptanceOfSubmission(@RequestBody Submission submission,
		@RequestHeader(value = "Authorization") String token)
	{
		try
		{
			User user = userService.getUserByToken(token.substring(7));

			if (!Objects.equals(user.getId(), submission.getSubmitterid()))
			{
				throw new ServiceException(ServiceExceptionType.UNAUTHORIZED_TO_CHANGE,
					"Changing others submission data is not allowed!"
				);
			}

			return submissionService.setAcceptance(submission.getId(), submission.getAcceptance());
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case UNAUTHORIZED_TO_CHANGE:
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Iterable<SubmissionListing> listSubmissions(@RequestParam(required = false, value = "id") Long id,
		@RequestParam(required = false, value = "taskid") Long taskid,
		@RequestParam(required = false, value = "description") String description,
		@RequestParam(required = false, value = "timeofsubmission") String timeofsubmission,
		@RequestParam(required = false, value = "acceptance") SubmissionAcceptanceEnum acceptance,
		@RequestParam(required = false, value = "submitterid") Long submitterid,
		@RequestParam(required = false, value = "submittername") String submittername)
	{
		if (id == null && taskid == null && description == null && timeofsubmission == null && acceptance == null
			&& submitterid == null && submittername == null)
		{
			return submissionService.getAllSubmissions();
		}

		return submissionService.getBySubmissionsObject(SubmissionListing.builder()
			.id(id)
			.taskid(taskid)
			.description(description != null && !description.isEmpty() ? description : null)
			.timeofsubmission(timeofsubmission != null && !timeofsubmission.isEmpty() ? LocalDate.parse(timeofsubmission) : null)
			.acceptance(acceptance)
			.submitterid(submitterid)
			.submittername(submittername)
			.build()
		);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSubmission(@PathVariable(value = "id") Long id,
		@RequestHeader(value = "Authorization") String token)
	{
		try
		{
			User user = userService.getUserByToken(token.substring(7));

			Submission submissionInDatabase = null;
			if (id != null)
			{
				submissionInDatabase = submissionService.getSubmissionById(id);
			}

			if (submissionInDatabase != null
				&& !Objects.equals(user.getId(), submissionInDatabase.getSubmitterid()))
			{
				throw new ServiceException(ServiceExceptionType.UNAUTHORIZED_TO_CHANGE,
					"Changing others submission data is not allowed!"
				);
			}

			submissionService.deleteSubmission(id);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case UNAUTHORIZED_TO_CHANGE:
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
				case ID_NOT_FOUND:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
