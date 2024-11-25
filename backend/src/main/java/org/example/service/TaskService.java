package org.example.service;

import org.example.model.Task;
import org.example.model.listing.TaskListingFilter;
import org.example.utils.exceptions.ServiceException;

public interface TaskService
{

	Iterable<Task> getAllTasks();

	Iterable<TaskListingFilter> getAllTasksAsListingFilter();

	Task getTaskById(Long id);

	Iterable<TaskListingFilter> getTasksByFilter(TaskListingFilter filter) throws ServiceException;

	Task saveTask(Task task) throws ServiceException;

	Task updateTask(Task task) throws ServiceException;

	Integer setMainTaskId(Long id, Long mainTaskId);

	void deleteTask(Long id);

	void deleteAll();
}
