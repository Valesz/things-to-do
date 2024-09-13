package org.example.service.impl;

import org.example.model.KeywordsForTasks;
import org.example.repository.KeywordsForTasksRepository;
import org.example.service.KeywordsForTasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class KeywordsForTasksServiceImpl implements KeywordsForTasksService {

    @Autowired
    private KeywordsForTasksRepository keywordsForTasksRepository;

    @Override
    public Iterable<KeywordsForTasks> getAllKeywordsForTasks() {
        return keywordsForTasksRepository.findAll();
    }

    @Override
    public KeywordsForTasks getKeywordForTaskById(Long id) {
        return keywordsForTasksRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<KeywordsForTasks> getKeywordsForTaskByTaskId(Long id) {
        return keywordsForTasksRepository.getKeywordsForTaskByTaskId(id);
    }

    @Override
    public Iterable<KeywordsForTasks> getTasksByKeyword(String keyword) {
        return keywordsForTasksRepository.getTasksByKeyword(keyword);
    }

    @Override
    public Iterable<KeywordsForTasks> getTasksByKeyword(Collection<String> keywords) {
        return keywordsForTasksRepository.getTasksByKeyword(keywords);
    }

    @Override
    public KeywordsForTasks saveKeywordForTask(KeywordsForTasks keywordForTask) {
        return keywordsForTasksRepository.save(keywordForTask);
    }

    @Override
    public boolean deleteKeywordForTask(Long id) {
        try {
            keywordsForTasksRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Deletion of " + id + " keyword for task failed:\n" + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteKeyword(String keyword) {
        try {
            keywordsForTasksRepository.deleteKeyword(keyword);
        } catch (Exception e) {
            System.out.println("Deletion of " + keyword + " keyword failed:\n" + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteAllKeywordsForAllTasks() {
        try {
            keywordsForTasksRepository.deleteAll();
        } catch (Exception e) {
            System.out.println("Deletion of all keywords for tasks failed:\n" + e.getMessage());
            return false;
        }

        return true;
    }
}
