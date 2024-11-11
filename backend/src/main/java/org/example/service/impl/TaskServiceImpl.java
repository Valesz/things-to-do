package org.example.service.impl;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Objects;
import org.example.repository.TaskRepository;
import org.example.model.Task;
import org.example.repository.UserRepository;
import org.example.model.TaskListingFilter;
import org.example.service.TaskService;
import org.example.utils.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class TaskServiceImpl implements TaskService
{

	@Resource(name = "taskRepository")
	private TaskRepository taskRepository;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Iterable<Task> getAllTasks()
	{
		return taskRepository.findAll();
	}

	@Override
	public Iterable<TaskListingFilter> getAllTasksAsListingFilter()
	{
		return taskRepository.getAllTasksAsListingFilter();
	}

	@Override
	public Task getTaskById(Long id)
	{
		return taskRepository.findById(id).orElse(null);
	}

	@Override
	public Iterable<TaskListingFilter> getTasksByFilter(TaskListingFilter filter) throws ServiceException
	{

		if (filter == null)
		{
			return getAllTasksAsListingFilter();
		}

		MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("id", filter.getId())
			.addValue("name", "%" + filter.getName() + "%")
			.addValue("description", filter.getDescription())
			.addValue("createdafter", filter.getCreatedAfter() == null ? LocalDate.EPOCH : filter.getCreatedAfter().toString())
			.addValue("createdbefore", filter.getCreatedBefore() == null ? LocalDate.MAX : filter.getCreatedBefore().toString())
			.addValue("maintaskid", filter.getMaintaskid())
			.addValue("ownerid", filter.getOwnerid())
			.addValue("userid", filter.getCompletedUserId());

		if (filter.getKeywords() != null)
		{
			Iterator<String> keywordsIterator = filter.getKeywords().iterator();
			for (int i = 0; keywordsIterator.hasNext(); i++)
			{
				params.addValue(String.format("keywords%d", i), "%" + keywordsIterator.next() + "%");
			}
		}

		String query = constructQueryByFilter(filter);

		return namedParameterJdbcTemplate.query(query, params, rs ->
		{
			List<TaskListingFilter> tasks = new ArrayList<>();

			while (rs.next())
			{
				tasks.add(TaskListingFilter.builder()
					.id(rs.getLong("id"))
					.name(rs.getString("name"))
					.description(rs.getString("description"))
					.timeofcreation(rs.getDate("timeofcreation").toLocalDate())
					.maintaskid(rs.getLong("maintaskid") == 0 ? null : rs.getLong("maintaskId"))
					.ownerid(rs.getLong("ownerId"))
					.keywordsMatching(rs.getLong("keywordsmatching"))
					.build());
			}

			return tasks;
		});
	}

	private String constructQueryByFilter(TaskListingFilter filter)
	{
		StringBuilder sb = new StringBuilder(" SELECT " +
			"TASK.ID, NAME, TASK.DESCRIPTION, TASK.TIMEOFCREATION, MAINTASKID, OWNERID, COUNT(*) AS KEYWORDSMATCHING " +
			"FROM \"task\" TASK");

		if (filter.getKeywords() != null)
		{
			sb.append(" INNER JOIN \"keywordsForTasks\" ON TASK.id = \"keywordsForTasks\".taskId ");
		}

		if (filter.getCompletedUserId() != null || filter.getCompleted() != null)
		{
			sb.append(" LEFT JOIN \"submission\" ON TASK.id = \"submission\".taskId ");
		}

		if (Boolean.FALSE.equals(filter.getCompleted()) && filter.getCompletedUserId() != null)
		{
			sb.append(" AND SUBMITTERID = :userid ");
		}

		sb.append(" WHERE 1 = 1 ");

		if (filter.getId() != null)
		{
			sb.append(" AND id = :id ");
		}

		if (filter.getName() != null)
		{
			sb.append(" AND name LIKE :name ");
		}

		if (filter.getDescription() != null)
		{
			sb.append(" AND description = :description ");
		}

		if (filter.getCreatedAfter() != null || filter.getCreatedBefore() != null)
		{
			sb.append(" AND TASK.timeofcreation BETWEEN :createdafter AND :createdbefore ");
		}

		//TODO: add null searchability
		if (filter.getMaintaskid() != null)
		{
			if (filter.getMaintaskid() <= 0)
			{
				sb.append(" AND maintaskid IS NULL ");
			}
			else
			{
				sb.append(" AND maintaskid = :maintaskid ");
			}
		}

		if (filter.getOwnerid() != null)
		{
			sb.append(" AND ownerid = :ownerid ");
		}

		if (filter.getKeywords() != null)
		{
			sb.append(" AND ( ");
			for (int i = 0; i < filter.getKeywords().size(); i++)
			{
				if (i != 0)
				{
					sb.append(" OR keyword LIKE :keywords");
				}
				else
				{
					sb.append(" keyword LIKE :keywords");
				}
				sb.append(i).append(" ");
			}
			sb.append(" ) ");
		}

		if (filter.getCompleted() != null)
		{
			if (filter.getCompleted() && filter.getCompletedUserId() != null)
			{
				sb.append(" AND SUBMITTERID = :userid ");
			}
			else
			{
				sb.append(" AND SUBMITTERID IS ");
				sb.append(filter.getCompleted() ? " NOT NULL " : " NULL ");
			}
		}

		sb.append(" GROUP BY TASK.id ");

		if (filter.getKeywords() != null)
		{
			sb.append(" ORDER BY KEYWORDSMATCHING DESC ");
		} else {
			sb.append(" ORDER BY TASK.TIMEOFCREATION DESC ");
		}

		return sb.toString();
	}

	@Override
	public Task saveTask(Task task) throws ServiceException
	{
		if (task.getId() != null)
		{
			throw new ServiceException(ServiceExceptionType.ID_GIVEN,
				"Remove id property, or use Update instead of Save."
			);
		}

		validateTaskProperties(task);

		return taskRepository.save(task);
	}

	@Override
	public Task updateTask(Task task) throws ServiceException
	{
		if (task.getId() == null)
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_GIVEN,
				"Id field must not be null"
			);
		}

		if (!taskRepository.existsById(task.getId()))
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_FOUND,
				"Task with id " + task.getId() + " doesn't exist. Please use save to save this instance."
			);
		}

		Task newTask = setNulLValues(task);

		validateTaskProperties(newTask);

		return taskRepository.save(newTask);
	}

	private Task setNulLValues(Task task)
	{
		Task taskInDb = taskRepository.findById(task.getId()).orElse(new Task());

		task.setName(task.getName() == null ? taskInDb.getName() : task.getName());

		task.setDescription(task.getDescription() == null ? taskInDb.getDescription() : task.getDescription());

		task.setTimeofcreation(task.getTimeofcreation() == null ? taskInDb.getTimeofcreation() : task.getTimeofcreation());

		task.setMaintaskid(task.getMaintaskid() == null ? taskInDb.getMaintaskid() : task.getMaintaskid());

		task.setOwnerid(task.getOwnerid() == null ? taskInDb.getOwnerid() : task.getOwnerid());

		return task;
	}

	private void validateTaskProperties(Task task) throws ServiceException
	{
		String errorMessage = checkForNullProperties(task);
		if (!errorMessage.isEmpty())
		{
			throw new ServiceException(ServiceExceptionType.NULL_ARGUMENT, errorMessage);
		}

		errorMessage = checkConstraints(task);
		if (!errorMessage.isEmpty())
		{
			throw new ServiceException(ServiceExceptionType.CONSTRAINT_VIOLATION, errorMessage);
		}
	}

	private String checkForNullProperties(Task task)
	{
		StringBuilder sb = new StringBuilder();

		if (task.getName() == null)
		{
			sb.append("name property not set, ");
		}

		if (task.getDescription() == null)
		{
			sb.append("description property not set, ");
		}

		if (task.getTimeofcreation() == null)
		{
			sb.append("timeofcreation property not set, ");
		}

		if (task.getOwnerid() == null)
		{
			sb.append("ownerid property not set, ");
		}

		return sb.toString();
	}

	private String checkConstraints(Task task)
	{
		StringBuilder sb = new StringBuilder();
		if (task.getMaintaskid() != null && !taskRepository.existsById(task.getMaintaskid()))
		{
			sb.append("maintaskid is not a valid task's ID, ");
		}

		if (task.getOwnerid() == null || !userRepository.existsById(task.getOwnerid()))
		{
			sb.append("ownerid is not a valid user's ID, ");
		}

		return sb.toString();
	}

	@Override
	public Integer setMainTaskId(Long id, Long mainTaskId)
	{
		if (id == null)
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_GIVEN,
				"Id field must not be null"
			);
		}

		if (!taskRepository.existsById(id))
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_FOUND,
				"Task with id " + id + " doesn't exist. Please use save to save this instance."
			);
		}

		if (mainTaskId != null && !taskRepository.existsById(mainTaskId))
		{
			throw new ServiceException(ServiceExceptionType.CONSTRAINT_VIOLATION,
				"MainTaskId is not a valid task's ID"
			);
		}

		if (Objects.equals(id, mainTaskId))
		{
			throw new ServiceException(ServiceExceptionType.CONSTRAINT_VIOLATION,
				"Id and mainTaskId should not be the same!"
			);
		}

		return taskRepository.setMainTaskId(id, mainTaskId);
	}

	@Override
	public void deleteTask(Long id) throws ServiceException
	{
		Task found = getTaskById(id);
		if (found == null)
		{
			throw new ServiceException(ServiceExceptionType.ID_NOT_FOUND,
				"Task with given ID does not exist"
			);
		}
		taskRepository.deleteById(id);
	}

	@Override
	public void deleteAll()
	{
		taskRepository.deleteAll();
	}
}
