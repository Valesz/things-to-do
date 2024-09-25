package org.example.service;

import org.example.model.CompletedTask;
import org.example.utils.exceptions.ServiceException;

public interface CompletedTasksService {

    Iterable<CompletedTask> getAllCompletedTasks();

    CompletedTask getCompletedTaskById(Long id);

    Iterable<CompletedTask> getByCompletedTasksObject(CompletedTask completedTask);

    CompletedTask saveCompletedTask(CompletedTask completedTask) throws ServiceException;

    CompletedTask updateCompletedTask(CompletedTask completedTask) throws ServiceException;

    void deleteCompletedTask(Long id);

    void deleteAll();

}
