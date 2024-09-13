package org.example.service;

import org.example.model.Task;

import java.util.List;

public interface TaskService {

    boolean assignUnderTask(Long subtaskId, Long maintaskId);

    Iterable<Task> getAllTasks();

    Task getTaskById(Long id);

    Iterable<Task> getTasksByFilter(Filter filter);

    List<Task> getCompletedTasksForUser(Long userId);

    Task saveTask(Task task);

    boolean deleteTask(Long id);

    boolean deleteAll();

}
