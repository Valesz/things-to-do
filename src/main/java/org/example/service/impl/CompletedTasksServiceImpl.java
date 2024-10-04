package org.example.service.impl;

import org.example.model.CompletedTask;
import org.example.repository.CompletedTasksRepository;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.service.CompletedTasksService;
import org.example.utils.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompletedTasksServiceImpl implements CompletedTasksService
{

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private CompletedTasksRepository completedTasksRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Override
	public Iterable<CompletedTask> getAllCompletedTasks()
	{
		return completedTasksRepository.findAll();
	}

	@Override
	public CompletedTask getCompletedTaskById(Long id)
	{
		return completedTasksRepository.findById(id).orElse(null);
	}

	@Override
	public Iterable<CompletedTask> getByCompletedTasksObject(CompletedTask completedTask)
	{
		if (completedTask == null)
		{
			return getAllCompletedTasks();
		}

		SqlParameterSource namedParams = new MapSqlParameterSource()
			.addValue("id", completedTask.getId())
			.addValue("userid", completedTask.getUserid())
			.addValue("taskid", completedTask.getTaskid());

		String query = constructQueryByOwnObject(completedTask);

		return namedParameterJdbcTemplate.query(query, namedParams, rs ->
		{
			List<CompletedTask> completedTaskList = new ArrayList<>();

			while (rs.next())
			{
				completedTaskList.add(CompletedTask.builder()
					.id(rs.getLong("id"))
					.userid(rs.getLong("userid"))
					.taskid(rs.getLong("taskid"))
					.build()
				);
			}

			return completedTaskList;
		});
	}

	private String constructQueryByOwnObject(CompletedTask completedTask)
	{
		StringBuilder query = new StringBuilder(" SELECT * FROM \"completedTasks\" ");

		query.append(" WHERE 1 = 1 ");

		if (completedTask.getId() != null)
		{
			query.append(" AND id = :id ");
		}

		if (completedTask.getUserid() != null)
		{
			query.append(" AND userid = :userid ");
		}

		if (completedTask.getTaskid() != null)
		{
			query.append(" AND taskid = :taskid ");
		}

		return query.toString();
	}

	@Override
	public CompletedTask saveCompletedTask(CompletedTask completedTask) throws ServiceException
	{
		if (completedTask.getId() != null)
		{
			throw new ServiceException(ServiceExceptionType.ILLEGAL_ID_ARGUMENT,
				"Remove id property, or use Update instead of Save."
			);
		}

		validateCompletedTaskProperties(completedTask);

		return completedTasksRepository.save(completedTask);
	}

	@Override
	public CompletedTask updateCompletedTask(CompletedTask completedTask) throws ServiceException
	{
		if (completedTask.getId() == null || !completedTasksRepository.existsById(completedTask.getId()))
		{
			throw new ServiceException(ServiceExceptionType.ILLEGAL_ID_ARGUMENT,
				"Completed task with id " + completedTask.getId() + " doesn't exist. Please use save to save this instance."
			);
		}

		CompletedTask newCompletedTask = setNulLValues(completedTask);

		validateCompletedTaskProperties(newCompletedTask);

		return completedTasksRepository.save(newCompletedTask);
	}

	private CompletedTask setNulLValues(CompletedTask completedTask)
	{
		CompletedTask completedTaskInDb = completedTasksRepository.findById(completedTask.getId()).orElse(new CompletedTask());

		completedTask.setUserid(completedTask.getUserid() == null ? completedTaskInDb.getUserid() : completedTask.getUserid());

		completedTask.setTaskid(completedTask.getTaskid() == null ? completedTaskInDb.getTaskid() : completedTask.getTaskid());

		return completedTask;
	}

	private void validateCompletedTaskProperties(CompletedTask completedTask) throws ServiceException
	{

		String errorMessage = checkForNullProperties(completedTask);
		if (!errorMessage.isEmpty())
		{
			throw new ServiceException(ServiceExceptionType.NULL_ARGUMENT, errorMessage);
		}

		errorMessage = checkConstraints(completedTask);
		if (!errorMessage.isEmpty())
		{
			throw new ServiceException(ServiceExceptionType.CONSTRAINT_VIOLATION, errorMessage);
		}
	}

	private String checkForNullProperties(CompletedTask completedTask)
	{
		StringBuilder errorMessage = new StringBuilder();

		if (completedTask.getUserid() == null)
		{
			errorMessage.append("userid property is not set, ");
		}

		if (completedTask.getTaskid() == null)
		{
			errorMessage.append("taskid property is not set, ");
		}

		return errorMessage.toString();
	}

	private String checkConstraints(CompletedTask completedTask)
	{
		StringBuilder errorMessage = new StringBuilder();

		if (!userRepository.existsById(completedTask.getUserid()))
		{
			errorMessage.append("userid property is not a valid user's ID, ");
		}

		if (!taskRepository.existsById(completedTask.getTaskid()))
		{
			errorMessage.append("taskid property is not a valid task's ID");
		}

		return errorMessage.toString();
	}

	@Override
	public void deleteCompletedTask(Long id) throws ServiceException
	{
		CompletedTask found = getCompletedTaskById(id);
		if (found == null)
		{
			throw new ServiceException(ServiceExceptionType.ILLEGAL_ID_ARGUMENT,
				"Completed Task with given id does not exist!"
			);
		}

		completedTasksRepository.deleteById(id);
	}

	@Override
	public void deleteAll()
	{
		completedTasksRepository.deleteAll();
	}
}
