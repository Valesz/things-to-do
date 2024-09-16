package org.example.service;

import org.example.model.CompletedTask;

public interface CompletedTasksService {

    Iterable<CompletedTask> getAllCompletedTasks();

    CompletedTask getCompletedTaskById(Long id);

    Iterable<CompletedTask> getByCompletedTasksObject(CompletedTask completedTask);

    CompletedTask saveCompletedTask(CompletedTask completedTask);

    boolean deleteCompletedTask(Long id);

    boolean deleteAll();

}
