package org.example.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.example.model.Submission;
import org.example.model.listing.SubmissionListing;
import org.example.repository.SubmissionRepository;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.service.SubmissionService;
import org.example.utils.enums.SubmissionAcceptanceEnum;
import org.example.utils.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService
{

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private SubmissionRepository submissionRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Integer setAcceptance(Long id, SubmissionAcceptanceEnum accepted)
	{
		if (id == null)
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_GIVEN,
				"Id field must not be null"
			);
		}

		if (!submissionRepository.existsById(id))
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_FOUND,
				"Submission with id " + id + " doesn't exist. Please use save to save this instance."
			);
		}

		return submissionRepository.setAcceptance(id, accepted);
	}

	@Override
	public Iterable<SubmissionListing> getAllSubmissions(long pageNumber, long pageSize)
	{
		return submissionRepository.getAllSubmissions(pageNumber * pageSize, pageSize);
	}

	@Override
	public Submission getSubmissionById(Long id)
	{
		return submissionRepository.findById(id).orElse(null);
	}

	@Override
	public Iterable<SubmissionListing> getBySubmissionsObject(SubmissionListing submission, long pageNumber, long pageSize)
	{
		if (submission == null)
		{
			return getAllSubmissions(pageNumber, pageSize);
		}

		SqlParameterSource namedParams = new MapSqlParameterSource()
			.addValue("id", submission.getId())
			.addValue("taskid", submission.getTaskid())
			.addValue("description", submission.getDescription())
			.addValue("timeofsubmission", submission.getTimeofsubmission() == null ? null : submission.getTimeofsubmission().toString())
			.addValue("acceptance", submission.getAcceptance() == null ? null : submission.getAcceptance().toString())
			.addValue("submitterid", submission.getSubmitterid())
			.addValue("submittername", "%" + submission.getSubmittername() + "%");

		String query = constructQueryByOwnObject(submission, pageNumber, pageSize, false);

		return namedParameterJdbcTemplate.query(query, namedParams, rs ->
		{
			List<SubmissionListing> submissionList = new ArrayList<>();

			while (rs.next())
			{
				SubmissionListing tmpSubmission = SubmissionListing.builder()
					.id(rs.getLong("id"))
					.taskid(rs.getLong("taskid"))
					.description(rs.getString("description"))
					.timeofsubmission(LocalDate.parse(rs.getString("timeofsubmission")))
					.acceptance(SubmissionAcceptanceEnum.valueOf(rs.getString("acceptance")))
					.submitterid(rs.getLong("submitterid"))
					.submittername(rs.getString("submittername"))
					.build();

				submissionList.add(tmpSubmission);
			}

			return submissionList;
		});
	}

	@Override
	public long getBySubmissionsObjectCount(SubmissionListing submission)
	{
		if (submission == null)
		{
			return submissionRepository.count();
		}

		SqlParameterSource namedParams = new MapSqlParameterSource()
			.addValue("id", submission.getId())
			.addValue("taskid", submission.getTaskid())
			.addValue("description", submission.getDescription())
			.addValue("timeofsubmission", submission.getTimeofsubmission() == null ? null : submission.getTimeofsubmission().toString())
			.addValue("acceptance", submission.getAcceptance() == null ? null : submission.getAcceptance().toString())
			.addValue("submitterid", submission.getSubmitterid())
			.addValue("submittername", "%" + submission.getSubmittername() + "%");

		String query = constructQueryByOwnObject(submission, -1L, -1L, true);

		Long result = namedParameterJdbcTemplate.queryForObject(query, namedParams, Long.class);

		return result == null ? -1L : result;
	}

	private String constructQueryByOwnObject(SubmissionListing submission, long pageNumber, long pageSize, boolean justCount)
	{
		StringBuilder query = new StringBuilder();

		if (!justCount)
		{
			query.append(
				" SELECT SUBMISSION.ID, TASKID, DESCRIPTION, SUBMISSION.TIMEOFSUBMISSION, ACCEPTANCE, SUBMITTERID, USERNAME AS SUBMITTERNAME FROM \"submission\" SUBMISSION "
			);
		} else {
			query.append("SELECT COUNT(SUBMISSION.ID) FROM \"submission\" SUBMISSION ");
		}

		query.append(" INNER JOIN \"user\" USERT ON SUBMISSION.submitterid = USERT.id ");

		query.append(" WHERE 1 = 1 ");

		if (submission.getId() != null)
		{
			query.append(" AND SUBMISSION.id = :id ");
		}

		if (submission.getTaskid() != null)
		{
			query.append(" AND taskid = :taskid ");
		}

		if (submission.getDescription() != null)
		{
			query.append(" AND description = :description ");
		}

		if (submission.getTimeofsubmission() != null)
		{
			query.append(" AND timeofsubmission = :timeofsubmission ");
		}

		if (submission.getAcceptance() != null)
		{
			query.append(" AND acceptance = :acceptance ");
		}

		if (submission.getSubmitterid() != null)
		{
			query.append(" AND submitterid = :submitterid ");
		}

		if (submission.getSubmittername() != null)
		{
			query.append(" AND USERT.username LIKE :submittername ");
		}

		if (!justCount)
		{
			query.append(" ORDER BY timeofsubmission DESC, ID DESC ");
		}

		if (pageNumber >= 0 && pageSize >= 0) {
			query.append(" LIMIT ").append(pageSize);

			query.append(" OFFSET ").append(pageNumber * pageSize);
		}

		return query.toString();
	}

	@Override
	public Submission saveSubmission(Submission submission) throws ServiceException
	{
		if (submission.getId() != null)
		{
			throw new ServiceException(ServiceExceptionType.ID_GIVEN,
				"Remove id property, or use Update instead of Save."
			);
		}

		validateSubmissionProperties(submission);

		return submissionRepository.save(submission);
	}

	@Override
	public Submission updateSubmission(Submission submission) throws ServiceException
	{
		if (submission.getId() == null)
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_GIVEN,
				"Id field must not be null"
			);
		}

		if (!submissionRepository.existsById(submission.getId()))
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_FOUND,
				"Submission with id " + submission.getId() + " doesn't exist. Please use save to save this instance."
			);
		}

		Submission newSubmission = setNulLValues(submission);

		validateSubmissionProperties(newSubmission);

		return submissionRepository.save(newSubmission);
	}

	private Submission setNulLValues(Submission submission)
	{
		Submission submissionInDb = submissionRepository.findById(submission.getId()).orElse(new Submission());

		submission.setTaskid(submission.getTaskid() == null ? submissionInDb.getTaskid() : submission.getTaskid());

		submission.setDescription(submission.getDescription() == null ? submissionInDb.getDescription() : submission.getDescription());

		submission.setTimeofsubmission(submission.getTimeofsubmission() == null ? submissionInDb.getTimeofsubmission() : submission.getTimeofsubmission());

		submission.setAcceptance(submission.getAcceptance() == null ? submissionInDb.getAcceptance() : submission.getAcceptance());

		submission.setSubmitterid(submission.getSubmitterid() == null ? submissionInDb.getSubmitterid() : submission.getSubmitterid());

		return submission;
	}

	private void validateSubmissionProperties(Submission submission) throws ServiceException
	{

		String errorMessage = checkForNullProperties(submission);
		if (StringUtils.isNotBlank(errorMessage))
		{
			throw new ServiceException(ServiceExceptionType.NULL_ARGUMENT, errorMessage);
		}

		errorMessage = checkConstraints(submission);
		if (StringUtils.isNotBlank(errorMessage))
		{
			throw new ServiceException(ServiceExceptionType.CONSTRAINT_VIOLATION, errorMessage);
		}
	}

	private String checkForNullProperties(Submission submission)
	{
		StringBuilder errorMessage = new StringBuilder();

		if (submission.getTaskid() == null)
		{
			errorMessage.append("taskid property is not set, ");
		}

		if (StringUtils.isBlank(submission.getDescription()))
		{
			errorMessage.append("description property is not set, ");
		}

		if (submission.getTimeofsubmission() == null)
		{
			errorMessage.append("timeofsubmission property is not set, ");
		}

		if (submission.getSubmitterid() == null)
		{
			errorMessage.append("submitterid property is not set, ");
		}

		return errorMessage.toString();
	}

	private String checkConstraints(Submission submission)
	{
		StringBuilder errorMessage = new StringBuilder();

		if (!taskRepository.existsById(submission.getTaskid()))
		{
			errorMessage.append("taskid property is not a valid task's ID, ");
		}

		if (!userRepository.existsById(submission.getSubmitterid()))
		{
			errorMessage.append("submitterid property is not a valid user's ID, ");
		}

		return errorMessage.toString();
	}

	@Override
	public void deleteSubmission(Long id)
	{
		Submission found = getSubmissionById(id);
		if (found == null)
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_FOUND,
				"Submission with id " + id + " doesn't exist."
			);
		}
		submissionRepository.deleteById(id);
	}

	@Override
	public void deleteAll()
	{
		submissionRepository.deleteAll();
	}
}
