package org.example.service;

import org.example.model.KeywordsForTasks;

import java.util.Collection;

public interface KeywordsForTasksService {

    Iterable<KeywordsForTasks> getAllKeywordsForTasks();

    KeywordsForTasks getKeywordForTaskById(Long id);

    Iterable<KeywordsForTasks> getByKeywordsForTasksObject(KeywordsForTasks keywordsForTasks);

    Iterable<KeywordsForTasks> getKeywordsForTaskByTaskId(Long id);

    Iterable<KeywordsForTasks> getTasksByKeyword(String keyword);

    Iterable<KeywordsForTasks> getTasksByKeyword(Collection<String> keyword);

    KeywordsForTasks saveKeywordForTask(KeywordsForTasks keywordForTask);

    Iterable<KeywordsForTasks> saveKeywordsForTasks(Iterable<KeywordsForTasks> keywordForTask);

    KeywordsForTasks updateKeywordForTask(KeywordsForTasks keywordsForTasks);

    boolean deleteKeywordForTask(Long id);

    boolean deleteKeyword(String keyword);

    boolean deleteAllKeywordsForAllTasks();

}
