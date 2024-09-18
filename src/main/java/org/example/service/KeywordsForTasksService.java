package org.example.service;

import org.example.model.KeywordsForTasks;
import org.example.utils.exceptions.ConstraintException;
import org.example.utils.exceptions.NullValueException;

import java.util.Collection;

public interface KeywordsForTasksService {

    Iterable<KeywordsForTasks> getAllKeywordsForTasks();

    KeywordsForTasks getKeywordForTaskById(Long id);

    Iterable<KeywordsForTasks> getByKeywordsForTasksObject(KeywordsForTasks keywordsForTasks);

    Iterable<KeywordsForTasks> getKeywordsForTaskByTaskId(Long id);

    Iterable<KeywordsForTasks> getTasksByKeyword(String keyword);

    Iterable<KeywordsForTasks> getTasksByKeyword(Collection<String> keyword);

    KeywordsForTasks saveKeywordForTask(KeywordsForTasks keywordForTask) throws NullValueException, ConstraintException;

    Iterable<KeywordsForTasks> saveKeywordsForTasks(Iterable<KeywordsForTasks> keywordForTask) throws NullValueException, ConstraintException;

    KeywordsForTasks updateKeywordForTask(KeywordsForTasks keywordsForTasks) throws NullValueException, ConstraintException;

    boolean deleteKeywordForTask(Long id);

    boolean deleteKeyword(String keyword);

    boolean deleteAllKeywordsForAllTasks();

}
