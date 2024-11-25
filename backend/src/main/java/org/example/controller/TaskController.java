package org.example.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.example.MyConfiguration;
import org.example.model.KeywordsForTasks;
import org.example.model.Task;
import org.example.model.listing.TaskListingFilter;
import org.example.model.User;
import org.example.service.KeywordsForTasksService;
import org.example.service.TaskService;
import org.example.service.UserService;
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
public class TaskController
{

	@Autowired
	TaskService taskService;

	@Autowired
	UserService userService;

	@Autowired
	KeywordsForTasksService keywordsForTasksService;

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Task addTask(@RequestBody Task task,
		@RequestHeader(name = "Authorization") String token)
	{
		try
		{
			//TODO: Validate ownerID with token
			return taskService.saveTask(task);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case ID_GIVEN:
				case NULL_ARGUMENT:
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

				case CONSTRAINT_VIOLATION:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@RequestMapping(value = "/", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public Task updateTask(@RequestBody Task task)
	{
		try
		{
			return taskService.updateTask(task);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case ID_NOT_GIVEN:
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

				case ID_NOT_FOUND:
				case CONSTRAINT_VIOLATION:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@RequestMapping(value = "/maintaskid", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public Integer updateMainTaskIdForTask(@RequestBody Task task)
	{
		try
		{
			return taskService.setMainTaskId(task.getId(), task.getMaintaskid());
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case NULL_ARGUMENT:
				case ID_NOT_GIVEN:
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

				case ID_NOT_FOUND:
				case CONSTRAINT_VIOLATION:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	//TODO: Write test for not completed tasks
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Iterable<TaskListingFilter> listTasksByFilter(@RequestParam(required = false, value = "keywords") Collection<String> keywords,
		@RequestParam(required = false, value = "id") Long id,
		@RequestParam(required = false, value = "name") String name,
		@RequestParam(required = false, value = "description") String description,
		@RequestParam(required = false, value = "createdafter") String createdafter,
		@RequestParam(required = false, value = "createdbefore") String createdbefore,
		@RequestParam(required = false, value = "maintaskid") Long maintaskid,
		@RequestParam(required = false, value = "ownerid") Long ownerid,
		@RequestParam(required = false, value = "ownername") String ownername,
		@RequestParam(required = false, value = "completeduserid") Long completeduserid,
		@RequestParam(required = false, value = "completed") Boolean completed,
		@RequestHeader(required = false, name = "Authorization") String token)
	{
		if (id == null && name == null && description == null
			&& createdafter == null && createdbefore == null && maintaskid == null
			&& ownerid == null && ownername == null && completeduserid == null
			&& keywords == null && completed == null)
		{
			Iterable<TaskListingFilter> tasks = taskService.getAllTasksAsListingFilter();

			for (TaskListingFilter task : tasks)
			{
				Iterable<KeywordsForTasks> keywordsForTask = keywordsForTasksService.getByKeywordsForTasksObject(
					KeywordsForTasks.builder()
						.taskid(task.getId())
						.build()
				);
				List<String> keywordsList = new ArrayList<>();

				for (KeywordsForTasks keyword : keywordsForTask)
				{
					keywordsList.add(keyword.getKeyword());
				}

				task.setKeywords(keywordsList);
			}

			return tasks;
		}

		if (ownerid == null && ownername != null)
		{
			Iterator<User> userIterator = userService.getByUsersObject(User.builder()
				.username(ownername).build()
			).iterator();

			if (userIterator.hasNext())
			{
				ownerid = userIterator.next().getId();
			}
		}

		Iterable<TaskListingFilter> tasks = taskService.getTasksByFilter(TaskListingFilter.builder()
			.keywords(keywords != null && !keywords.isEmpty() ? keywords : null)
			.id(id)
			.name(name != null && !name.isEmpty() ? name : null)
			.description(description != null && !description.isEmpty() ? description : null)
			.createdAfter(createdafter != null && !createdafter.isEmpty() ? LocalDate.parse(createdafter) : null)
			.createdBefore(createdbefore != null && !createdbefore.isEmpty() ? LocalDate.parse(createdbefore) : null)
			.maintaskid(maintaskid)
			.ownerid(ownerid)
			.completedUserId(completeduserid)
			.completed(completed)
			.build()
		);

		for (TaskListingFilter task : tasks)
		{
			Iterable<KeywordsForTasks> keywordsForTask = keywordsForTasksService.getByKeywordsForTasksObject(
				KeywordsForTasks.builder()
					.taskid(task.getId())
					.build()
			);
			List<String> keywordsList = new ArrayList<>();

			for (KeywordsForTasks keyword : keywordsForTask)
			{
				keywordsList.add(keyword.getKeyword());
			}

			task.setKeywords(keywordsList);
		}

		return tasks;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTask(@PathVariable(value = "id") Long taskId)
	{
		try
		{
			taskService.deleteTask(taskId);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case ID_NOT_FOUND:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@RequestMapping(value = "/keyword/", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Iterable<KeywordsForTasks> addKeyword(@RequestBody Iterable<KeywordsForTasks> keyword)
	{
		try
		{
			return keywordsForTasksService.saveKeywordsForTasks(keyword);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case ID_GIVEN:
				case NULL_ARGUMENT:
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

				case CONSTRAINT_VIOLATION:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());

				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@RequestMapping(value = "/keyword/", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public Iterable<KeywordsForTasks> listKeywordsForTasks(@RequestParam(required = false, value = "id") Long id,
		@RequestParam(required = false, value = "taskid") Long taskid,
		@RequestParam(required = false, value = "keyword") String keyword)
	{
		return keywordsForTasksService.getByKeywordsForTasksObject(KeywordsForTasks.builder()
			.id(id)
			.taskid(taskid)
			.keyword(keyword != null && !keyword.isEmpty() ? keyword : null)
			.build()
		);
	}

	@RequestMapping(value = "/keyword/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteKeyword(@PathVariable(value = "id") Long id)
	{
		try
		{
			keywordsForTasksService.deleteKeywordForTask(id);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case ID_NOT_FOUND:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@RequestMapping(value = "{id}/keyword", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteKeywordsByTaskid(@PathVariable(value = "id") Long taskId)
	{
		try
		{
			keywordsForTasksService.deleteKeywordsByTaskId(taskId);
		}
		catch (ServiceException e)
		{
			switch (e.getServiceExceptionTypeEnum())
			{
				case ID_NOT_FOUND:
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
				default:
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		catch (Exception e)
		{
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
