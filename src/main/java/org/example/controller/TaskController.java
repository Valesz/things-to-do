package org.example.controller;

import org.example.MyConfiguration;
import org.example.model.CompletedTask;
import org.example.model.KeywordsForTasks;
import org.example.model.Task;
import org.example.service.CompletedTasksService;
import org.example.service.Filter;
import org.example.service.KeywordsForTasksService;
import org.example.service.TaskService;
import org.example.utils.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/api/task")
@Import(value = {MyConfiguration.class})
public class TaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    CompletedTasksService completedTasksService;

    @Autowired
    KeywordsForTasksService keywordsForTasksService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
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
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

        }
    }

    //TODO: Figure out how to handle 2 -> null value change.
    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
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
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Task> listTasks(@RequestParam(required = false, value = "id") Long id,
                                    @RequestParam(required = false, value = "name") String name,
                                    @RequestParam(required = false, value = "description") String description,
                                    @RequestParam(required = false, value = "timeofcreation") String timeofcreation,
                                    @RequestParam(required = false, value = "maintaskid") Long maintaskid,
                                    @RequestParam(required = false, value = "ownerid") Long ownerid) {
        return taskService.getByTasksObject(Task.builder()
                .id(id)
                .name(name != null && !name.isEmpty() ? name : null)
                .description(description != null && !description.isEmpty() ? description : null)
                .timeofcreation(timeofcreation != null && !timeofcreation.isEmpty() ? LocalDate.parse(timeofcreation) : null)
                .maintaskid(maintaskid)
                .ownerid(ownerid)
                .build()
        );
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Task> listTasksByFilter(@RequestParam(required = false, value = "keywords") Collection<String> keywords,
                                            @RequestParam(required = false, value = "name") String name,
                                            @RequestParam(required = false, value = "ownerid") Long ownerid,
                                            @RequestParam(required = false, value = "completeduserid") Long completeduserid) {
        return taskService.getTasksByFilter(Filter.builder()
                .keywords(!keywords.isEmpty() ? keywords : null)
                .name(name != null && !name.isEmpty() ? name : null)
                .ownerId(ownerid)
                .completedUserId(completeduserid)
                .build()
        );
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable(value = "id") Long taskId) {
        try {
            taskService.deleteTask(taskId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/completed/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
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
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/completed/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<CompletedTask> listCompletedTasks(@RequestParam(required = false, value = "taskid") Long taskid,
                                                      @RequestParam(required = false, value = "userid") Long userid) {
        return completedTasksService.getByCompletedTasksObject(CompletedTask.builder()
                .taskid(taskid)
                .userid(userid)
                .build()
        );
    }

    @RequestMapping(value = "/completed/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompleteTask(@PathVariable(value = "id") long id) {
        try {
            completedTasksService.deleteCompletedTask(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/keyword/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
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
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/keyword/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<KeywordsForTasks> listKeywordsForTasks(@RequestParam(required = false, value = "id") Long id,
                                                           @RequestParam(required = false, value = "taskid") Long taskid,
                                                           @RequestParam(required = false, value = "keyword") String keyword) {
        return keywordsForTasksService.getByKeywordsForTasksObject(KeywordsForTasks.builder()
                .id(id)
                .taskid(taskid)
                .keyword(keyword != null && !keyword.isEmpty() ? keyword : null)
                .build()
        );
    }

    @RequestMapping(value = "/keyword/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKeyword(@PathVariable(value = "id") Long id) {
        try {
            keywordsForTasksService.deleteKeywordForTask(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
