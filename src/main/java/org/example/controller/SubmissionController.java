package org.example.controller;

import org.example.MyConfiguration;
import org.example.model.Submission;
import org.example.service.SubmissionService;
import org.example.utils.exceptions.ServiceException;
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

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Submission addSubmission(@RequestBody Submission submission)
	{
		try
		{
			return submissionService.saveSubmission(submission);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case ID_GIVEN:
				case NULL_ARGUMENT:
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
	public Submission updateSubmission(@RequestBody Submission submission)
	{
		try
		{
			return submissionService.updateSubmission(submission);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case ID_NOT_GIVEN:
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

				case CONSTRAINT_VIOLATION:
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

	@RequestMapping(value = "/acceptance", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public Integer updateAcceptanceOfSubmission(@RequestBody Submission submission)
	{
		return submissionService.setAcceptance(submission.getId(), submission.getAcceptance());
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Iterable<Submission> listSubmissions(@RequestParam(required = false, value = "id") Long id,
		@RequestParam(required = false, value = "taskid") Long taskid,
		@RequestParam(required = false, value = "description") String description,
		@RequestParam(required = false, value = "timeofsubmission") String timeofsubmission,
		@RequestParam(required = false, value = "acceptance") Boolean acceptance,
		@RequestParam(required = false, value = "submitterid") Long submitterid)
	{
		return submissionService.getBySubmissionsObject(Submission.builder()
			.id(id)
			.taskid(taskid)
			.description(description != null && !description.isEmpty() ? description : null)
			.timeofsubmission(timeofsubmission != null && !timeofsubmission.isEmpty() ? LocalDate.parse(timeofsubmission) : null)
			.acceptance(acceptance)
			.submitterid(submitterid)
			.build()
		);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSubmission(@PathVariable(value = "id") Long id)
	{
		try
		{
			submissionService.deleteSubmission(id);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
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
