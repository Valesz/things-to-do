package org.example.service;

import org.example.model.CompletedTask;
import org.example.utils.exceptions.ConstraintException;
import org.example.utils.exceptions.NullValueException;

public interface CompletedTasksService {

    Iterable<CompletedTask> getAllCompletedTasks();

    CompletedTask getCompletedTaskById(Long id);

    Iterable<CompletedTask> getByCompletedTasksObject(CompletedTask completedTask);

    CompletedTask saveCompletedTask(CompletedTask completedTask) throws NullValueException, ConstraintException;

    CompletedTask updateCompletedTask(CompletedTask completedTask) throws NullValueException, ConstraintException;

    boolean deleteCompletedTask(Long id);

    boolean deleteAll();

}
