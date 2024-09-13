package org.example.service.impl;

import org.example.model.CompletedTask;
import org.example.repository.CompletedTasksRepository;
import org.example.service.CompletedTasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompletedTasksServiceImpl implements CompletedTasksService {

    @Autowired
    private CompletedTasksRepository completedTasksRepository;


    @Override
    public Iterable<CompletedTask> getAllCompletedTasks() {
        return completedTasksRepository.findAll();
    }

    @Override
    public CompletedTask getCompletedTaskById(Long id) {
        return completedTasksRepository.findById(id).orElse(null);
    }

    @Override
    public CompletedTask saveCompletedTask(CompletedTask completedTask) {
        return completedTasksRepository.save(completedTask);
    }

    @Override
    public boolean deleteCompletedTask(Long id) {
        try {
            completedTasksRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Deletion of " + id + " user failed:\n" + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteAll() {
        try {
            completedTasksRepository.deleteAll();
        } catch (Exception e) {
            System.out.println("Deletion of all users failed:\n" + e.getMessage());
            return false;
        }

        return true;
    }
}
