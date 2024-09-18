package org.example.controller;

import org.example.model.CompletedTask;
import org.example.model.KeywordsForTasks;
import org.example.model.Task;
import org.example.service.CompletedTasksService;
import org.example.service.Filter;
import org.example.service.KeywordsForTasksService;
import org.example.service.TaskService;
import org.example.utils.exceptions.ConstraintException;
import org.example.utils.exceptions.NullValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    CompletedTasksService completedTasksService;

    @Autowired
    KeywordsForTasksService keywordsForTasksService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Task addTask(@RequestBody Task task) {
        try {
            return taskService.saveTask(task);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (NullValueException | ConstraintException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Task updateTask(@RequestBody Task task) {
        try {
            return taskService.updateTask(task);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (NullValueException | ConstraintException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Iterable<Task> listTasks(@RequestBody(required = false) Task task) {
        return taskService.getByTasksObject(task);
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    public Iterable<Task> listTasksByFilter(@RequestBody Filter filter) {
        return taskService.getTasksByFilter(filter);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public boolean deleteTask(@PathVariable(value = "id") Long taskId) {
        return taskService.deleteTask(taskId);
    }

    @RequestMapping(value = "/completed", method = RequestMethod.POST)
    public CompletedTask addCompletedTask(@RequestBody CompletedTask completedTask) {
        try {
            return completedTasksService.saveCompletedTask(completedTask);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (NullValueException | ConstraintException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @RequestMapping(value = "/completed", method = RequestMethod.GET)
    public Iterable<CompletedTask> listCompletedTasks(@RequestBody(required = false) CompletedTask completedTask) {
        return completedTasksService.getByCompletedTasksObject(completedTask);
    }

    @RequestMapping(value = "/completed/{id}", method = RequestMethod.DELETE)
    public boolean deleteCompleteTask(@PathVariable(value = "id") long id) {
        try {
            return completedTasksService.deleteCompletedTask(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @RequestMapping(value = "/keyword", method = RequestMethod.POST)
    public Iterable<KeywordsForTasks> addKeyword(@RequestBody Iterable<KeywordsForTasks> keyword) {
        try {
            return keywordsForTasksService.saveKeywordsForTasks(keyword);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (NullValueException | ConstraintException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @RequestMapping(value = "/keyword", method = RequestMethod.GET)
    public Iterable<KeywordsForTasks> listKeywordsForTasks(@RequestBody(required = false) KeywordsForTasks keyword) {
        return keywordsForTasksService.getByKeywordsForTasksObject(keyword);
    }

    @RequestMapping(value = "/keyword/{id}", method = RequestMethod.DELETE)
    public boolean deleteKeyword(@PathVariable(value = "id") Long id) {
        return keywordsForTasksService.deleteKeywordForTask(id);
    }

}
