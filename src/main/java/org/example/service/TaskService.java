package org.example.service;

import org.example.model.Task;
import org.example.utils.exceptions.ServiceException;

public interface TaskService
{

	Iterable<Task> getAllTasks();

	Task getTaskById(Long id);

	Iterable<Task> getByTasksObject(Task task);

	Iterable<Task> getTasksByFilter(Filter filter) throws ServiceException;

	Task saveTask(Task task) throws ServiceException;

	Task updateTask(Task task) throws ServiceException;

	void deleteTask(Long id);

	void deleteAll();
}
