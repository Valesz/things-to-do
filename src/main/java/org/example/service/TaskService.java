package org.example.service;

import org.example.model.Task;
import org.example.utils.exceptions.ConstraintException;
import org.example.utils.exceptions.NullValueException;

import java.util.List;

public interface TaskService {

    boolean assignUnderTask(Long subtaskId, Long maintaskId);

    Iterable<Task> getAllTasks();

    Task getTaskById(Long id);

    Iterable<Task> getByTasksObject(Task task);

    Iterable<Task> getTasksByFilter(Filter filter);

    List<Task> getCompletedTasksForUser(Long userId);

    Task saveTask(Task task) throws NullValueException, ConstraintException;

    Task updateTask(Task task) throws NullValueException, ConstraintException;

    boolean deleteTask(Long id);

    boolean deleteAll();

}
