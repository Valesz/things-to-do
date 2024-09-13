package org.example.service;

import org.example.model.CompletedTask;

public interface CompletedTasksService {

    Iterable<CompletedTask> getAllCompletedTasks();

    CompletedTask getCompletedTaskById(Long id);

    CompletedTask saveCompletedTask(CompletedTask completedTask);

    boolean deleteCompletedTask(Long id);

    boolean deleteAll();

}
