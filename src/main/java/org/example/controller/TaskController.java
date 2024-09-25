package org.example.controller;

import org.example.model.CompletedTask;
import org.example.model.KeywordsForTasks;
import org.example.model.Task;
import org.example.service.CompletedTasksService;
import org.example.service.Filter;
import org.example.service.KeywordsForTasksService;
import org.example.service.TaskService;
import org.example.utils.exceptions.ServiceException;
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
        } catch (ServiceException e) {
            switch (e.getServiceExceptionTypeEnum()) {
                case NULL_ARGUMENT:
                case CONSTRAINT_VIOLATION:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                case ILLEGAL_ID_ARGUMENT:
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
                default:
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (Exception e) {

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

        }
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Task updateTask(@RequestBody Task task) {
        try {
            return taskService.updateTask(task);
        } catch (ServiceException e) {
            switch (e.getServiceExceptionTypeEnum()) {
                case NULL_ARGUMENT:
                case CONSTRAINT_VIOLATION:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                case ILLEGAL_ID_ARGUMENT:
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                default:
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
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
    public void deleteTask(@PathVariable(value = "id") Long taskId) {
        try {
            taskService.deleteTask(taskId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/completed", method = RequestMethod.POST)
    public CompletedTask addCompletedTask(@RequestBody CompletedTask completedTask) {
        try {
            return completedTasksService.saveCompletedTask(completedTask);
        } catch (ServiceException e) {
            switch (e.getServiceExceptionTypeEnum()) {
                case NULL_ARGUMENT:
                case CONSTRAINT_VIOLATION:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                case ILLEGAL_ID_ARGUMENT:
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
                default:
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/completed", method = RequestMethod.GET)
    public Iterable<CompletedTask> listCompletedTasks(@RequestBody(required = false) CompletedTask completedTask) {
        return completedTasksService.getByCompletedTasksObject(completedTask);
    }

    @RequestMapping(value = "/completed/{id}", method = RequestMethod.DELETE)
    public void deleteCompleteTask(@PathVariable(value = "id") long id) {
        try {
            completedTasksService.deleteCompletedTask(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/keyword", method = RequestMethod.POST)
    public Iterable<KeywordsForTasks> addKeyword(@RequestBody Iterable<KeywordsForTasks> keyword) {
        try {
            return keywordsForTasksService.saveKeywordsForTasks(keyword);
        } catch (ServiceException e) {
            switch (e.getServiceExceptionTypeEnum()) {
                case NULL_ARGUMENT:
                case CONSTRAINT_VIOLATION:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

                case ILLEGAL_ID_ARGUMENT:
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());

                default:
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/keyword", method = RequestMethod.GET)
    public Iterable<KeywordsForTasks> listKeywordsForTasks(@RequestBody(required = false) KeywordsForTasks keyword) {
        return keywordsForTasksService.getByKeywordsForTasksObject(keyword);
    }

    @RequestMapping(value = "/keyword/{id}", method = RequestMethod.DELETE)
    public void deleteKeyword(@PathVariable(value = "id") Long id) {
        try {
            keywordsForTasksService.deleteKeywordForTask(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
