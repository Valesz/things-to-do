package org.example.service;

import org.example.model.KeywordsForTasks;
import org.example.utils.exceptions.ServiceException;

public interface KeywordsForTasksService {

    Iterable<KeywordsForTasks> getAllKeywordsForTasks();

    KeywordsForTasks getKeywordForTaskById(Long id);

    Iterable<KeywordsForTasks> getByKeywordsForTasksObject(KeywordsForTasks keywordsForTasks);

    KeywordsForTasks saveKeywordForTask(KeywordsForTasks keywordForTask) throws ServiceException;

    Iterable<KeywordsForTasks> saveKeywordsForTasks(Iterable<KeywordsForTasks> keywordForTask) throws ServiceException;

    KeywordsForTasks updateKeywordForTask(KeywordsForTasks keywordsForTasks) throws ServiceException;

    void deleteKeywordForTask(Long id);

    void deleteKeyword(String keyword);

    void deleteAllKeywordsForAllTasks();

}
