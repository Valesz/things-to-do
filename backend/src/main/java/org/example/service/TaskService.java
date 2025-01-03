package org.example.service;

import org.example.model.Task;
import org.example.model.listing.TaskListingFilter;
import org.example.utils.exceptions.ServiceException;
import org.springframework.stereotype.Service;

@Service
public interface TaskService
{
	Iterable<Task> getAllTasks();

	Iterable<TaskListingFilter> getAllTasksAsListingFilter(long pageNumber, long pageSize);

	Task getTaskById(Long id);

	Iterable<TaskListingFilter> getTasksByFilter(TaskListingFilter filter, long pageNumber, long pageSize) throws ServiceException;

	long getTasksByFilterCount(TaskListingFilter filter) throws ServiceException;

	Task saveTask(Task task) throws ServiceException;

	Task updateTask(Task task) throws ServiceException;

	Integer setMainTaskId(Long id, Long mainTaskId);

	void deleteTask(Long id);

	void deleteAll();
}
