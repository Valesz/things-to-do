package org.example.service.impl;

import org.example.model.KeywordsForTasks;
import org.example.model.Task;
import org.example.repository.KeywordsForTasksRepository;
import org.example.repository.TaskRepository;
import org.example.service.KeywordsForTasksService;
import org.example.utils.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeywordsForTasksServiceImpl implements KeywordsForTasksService
{

	@Autowired
	private KeywordsForTasksRepository keywordsForTasksRepository;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private TaskRepository taskRepository;

	@Override
	public Iterable<KeywordsForTasks> getAllKeywordsForTasks()
	{
		return keywordsForTasksRepository.findAll();
	}

	@Override
	public KeywordsForTasks getKeywordForTaskById(Long id)
	{
		return keywordsForTasksRepository.findById(id).orElse(null);
	}

	@Override
	public Iterable<KeywordsForTasks> getByKeywordsForTasksObject(KeywordsForTasks keywordsForTasks)
	{
		if (keywordsForTasks == null)
		{
			return getAllKeywordsForTasks();
		}

		SqlParameterSource namedParams = new MapSqlParameterSource()
			.addValue("id", keywordsForTasks.getId())
			.addValue("taskid", keywordsForTasks.getTaskid())
			.addValue("keyword", keywordsForTasks.getKeyword());

		String query = constructQueryForGetByTaskObject(keywordsForTasks);

		return namedParameterJdbcTemplate.query(query, namedParams, rs ->
		{
			List<KeywordsForTasks> keywordsForTasksList = new ArrayList<>();

			while (rs.next())
			{
				keywordsForTasksList.add(KeywordsForTasks.builder()
					.id(rs.getLong("id"))
					.taskid(rs.getLong("taskid"))
					.keyword(rs.getString("keyword"))
					.build()
				);
			}

			return keywordsForTasksList;
		});
	}

	private String constructQueryForGetByTaskObject(KeywordsForTasks keywordsForTasks)
	{
		StringBuilder query = new StringBuilder(" SELECT * FROM \"keywordsForTasks\" ");

		query.append(" WHERE 1 = 1 ");

		if (keywordsForTasks.getId() != null)
		{
			query.append(" AND id = :id ");
		}

		if (keywordsForTasks.getTaskid() != null)
		{
			query.append(" AND taskid = :taskid ");
		}

		if (keywordsForTasks.getKeyword() != null)
		{
			query.append(" AND keyword = :keyword ");
		}

		return query.toString();
	}

	@Override
	public KeywordsForTasks saveKeywordForTask(KeywordsForTasks keywordForTask) throws ServiceException
	{
		if (keywordForTask.getId() != null)
		{
			throw new ServiceException(ServiceExceptionType.ID_GIVEN,
				"Remove id property, or use Update instead of Save."
			);
		}

		validateKeywordsForTasksProperties(keywordForTask);

		return keywordsForTasksRepository.save(keywordForTask);
	}

	@Override
	public Iterable<KeywordsForTasks> saveKeywordsForTasks(Iterable<KeywordsForTasks> keywordForTask) throws ServiceException
	{
		for (KeywordsForTasks item : keywordForTask)
		{
			if (item.getId() != null)
			{
				throw new ServiceException(ServiceExceptionType.ID_GIVEN,
					"Remove id property, or use Update instead of Save."
				);
			}

			validateKeywordsForTasksProperties(item);
		}

		return keywordsForTasksRepository.saveAll(keywordForTask);
	}

	@Override
	public KeywordsForTasks updateKeywordForTask(KeywordsForTasks keywordForTask) throws ServiceException
	{
		if (keywordForTask.getId() == null)
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_GIVEN,
				"Id field must not be null"
			);
		}

		if (!keywordsForTasksRepository.existsById(keywordForTask.getId()))
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_FOUND,
				"Keyword for task with id " + keywordForTask.getId() + " doesn't exist. Please use save to save this instance."
			);
		}

		KeywordsForTasks newKeywordForTask = setNulLValues(keywordForTask);

		validateKeywordsForTasksProperties(newKeywordForTask);

		return keywordsForTasksRepository.save(newKeywordForTask);
	}

	private KeywordsForTasks setNulLValues(KeywordsForTasks keywordForTask)
	{
		KeywordsForTasks keywordForTaskInDb = keywordsForTasksRepository.findById(keywordForTask.getId()).orElse(new KeywordsForTasks());

		keywordForTask.setTaskid(keywordForTask.getTaskid() == null ? keywordForTaskInDb.getTaskid() : keywordForTask.getTaskid());

		keywordForTask.setKeyword(keywordForTask.getKeyword() == null ? keywordForTaskInDb.getKeyword() : keywordForTask.getKeyword());

		return keywordForTask;
	}

	private void validateKeywordsForTasksProperties(KeywordsForTasks keywordsForTasks) throws ServiceException
	{

		String errorMessage = checkForNullProperties(keywordsForTasks);
		if (!errorMessage.isEmpty())
		{
			throw new ServiceException(ServiceExceptionType.NULL_ARGUMENT, errorMessage);
		}

		errorMessage = checkConstraints(keywordsForTasks);
		if (!errorMessage.isEmpty())
		{
			throw new ServiceException(ServiceExceptionType.CONSTRAINT_VIOLATION, errorMessage);
		}
	}

	private String checkForNullProperties(KeywordsForTasks keywordsForTasks)
	{
		StringBuilder errorMessage = new StringBuilder();

		if (keywordsForTasks.getTaskid() == null)
		{
			errorMessage.append("taskid property is not set, ");
		}

		if (keywordsForTasks.getKeyword() == null)
		{
			errorMessage.append("keyword property is not set, ");
		}

		return errorMessage.toString();
	}

	private String checkConstraints(KeywordsForTasks keywordsForTasks)
	{
		StringBuilder errorMessage = new StringBuilder();

		if (!taskRepository.existsById(keywordsForTasks.getTaskid()))
		{
			errorMessage.append("taskid property is not a valid task's ID");
		}

		return errorMessage.toString();
	}

	@Override
	public void deleteKeywordForTask(Long id) throws ServiceException
	{
		KeywordsForTasks found = getKeywordForTaskById(id);
		if (found == null)
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_FOUND,
				"Keyword with id " + id + " doesn't exist."
			);
		}
		keywordsForTasksRepository.deleteById(id);
	}

	@Override
	public void deleteKeyword(String keyword)
	{
		keywordsForTasksRepository.deleteKeyword(keyword);
	}

	@Override
	public void deleteKeywordsByTaskId(long taskId)
	{
		Task found = taskRepository.findById(taskId).orElse(null);

		if (found == null)
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_FOUND,
				"Task with id " + taskId + " doesn't exist."
			);
		}

		keywordsForTasksRepository.deleteKeywordsByTaskId(taskId);
	}

	@Override
	public void deleteAllKeywordsForAllTasks()
	{
		keywordsForTasksRepository.deleteAll();
	}
}
