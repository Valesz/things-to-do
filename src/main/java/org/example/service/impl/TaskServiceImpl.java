package org.example.service.impl;

import org.example.repository.TaskRepository;
import org.example.model.Task;
import org.example.repository.UserRepository;
import org.example.service.Filter;
import org.example.service.TaskService;
import org.example.utils.exceptions.ConstraintException;
import org.example.utils.exceptions.NullValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class TaskServiceImpl implements TaskService {

    @Resource(name = "taskRepository")
    private TaskRepository taskRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean assignUnderTask(Long subtaskId, Long maintaskId) {
        return taskRepository.assignUnderTask(subtaskId, maintaskId);
    }

    @Override
    public Iterable<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<Task> getByTasksObject(Task task) {

        if (task == null) {
            return getAllTasks();
        }

        SqlParameterSource namedParams = new MapSqlParameterSource()
                .addValue("id", task.getId())
                .addValue("name", task.getName())
                .addValue("description", task.getDescription())
                .addValue("timeofcreation", task.getTimeofcreation() == null ? null : task.getTimeofcreation().toString())
                .addValue("maintaskid", task.getMaintaskid())
                .addValue("ownerid", task.getOwnerid());

        String query = contructQueryForGetByTaskObject(task);

        return namedParameterJdbcTemplate.query(query, namedParams, rs -> {
            List<Task> taskList = new ArrayList<>();

            while (rs.next()) {
                taskList.add(Task.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .timeofcreation(LocalDate.parse(rs.getString("timeofcreation")))
                        .maintaskid(task.getMaintaskid())
                        .ownerid(task.getOwnerid())
                        .build()
                );
            }

            return taskList;
        });
    }

    private String contructQueryForGetByTaskObject(Task task) {
        StringBuilder query = new StringBuilder(" SELECT * FROM \"task\" ");

        query.append(" WHERE 1 = 1 ");

        if (task.getId() != null) {
            query.append(" AND id = :id ");
        }

        if (task.getName() != null) {
            query.append(" AND name = :name ");
        }

        if (task.getDescription() != null) {
            query.append(" AND description = :description ");
        }

        if (task.getTimeofcreation() != null) {
            query.append(" AND timeofcreation = :timeofcreation ");
        }

        if (task.getMaintaskid() != null) {
            query.append(" AND maintaskid = :maintaskid ");
        }

        if (task.getOwnerid() != null) {
            query.append(" AND ownerid = :ownerid ");
        }

        return query.toString();
    }

    @Override
    public Iterable<Task> getTasksByFilter(Filter filter) {

        if (filter == null) {
            throw new IllegalArgumentException("filter cannot be null");
        }

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", filter.getName())
                .addValue("ownerId", filter.getOwnerId())
                .addValue("keywords", filter.getKeywords())
                .addValue("userId", filter.getCompletedUserId());

        String query = constructQueryByFilter(filter);

        return namedParameterJdbcTemplate.query(query, params, rs -> {
            List<Task> tasks = new ArrayList<>();

            while (rs.next()) {
                tasks.add(Task.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .timeofcreation(rs.getDate("timeOfCreation").toLocalDate())
                        .maintaskid(rs.getLong("mainTaskId") == 0 ? null : rs.getLong("maintaskId"))
                        .ownerid(rs.getLong("ownerId"))
                        .build());
            }

            return tasks;
        });
    }

    private String constructQueryByFilter(Filter filter) {
        StringBuilder sb = new StringBuilder(" SELECT " +
                "\"task\".id, \"task\".name, \"task\".description, \"task\".timeOfCreation, \"task\".mainTaskId, \"task\".ownerId " +
                "FROM \"task\" ");

        if (filter.getKeywords() != null) {
            sb.append(" INNER JOIN \"keywordsForTasks\" ON \"task\".id = \"keywordsForTasks\".taskId ");
        }

        if (filter.getCompletedUserId() != null) {
            sb.append(" INNER JOIN \"completedTasks\" ON \"task\".id = \"completedTasks\".taskId ");
        }

        sb.append(" WHERE 1 = 1 ");

        if (filter.getName() != null) {
            sb.append(" AND name LIKE :name ");
        }

        if (filter.getOwnerId() != null) {
            sb.append(" AND ownerId = :ownerId ");
        }

        if (filter.getKeywords() != null) {
            sb.append(" AND keyword IN ( :keywords )");
        }

        if (filter.getCompletedUserId() != null) {
            sb.append(" AND \"completedTasks\".userId = :userId ");
        }

        sb.append(" GROUP BY \"task\".id ORDER BY \"task\".id ASC ");

        return sb.toString();
    }

    @Override
    public List<Task> getCompletedTasksForUser(Long userId) {
        return taskRepository.getCompletedTasksForUser(userId);
    }

    @Override
    public Task saveTask(Task task) throws IllegalArgumentException, NullValueException, ConstraintException {
        if (task.getId() != null) {
            throw new IllegalArgumentException("Remove id property, or use Update instead of Save.");
        }

        validateTaskProperties(task);

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Task task) throws IllegalArgumentException, NullValueException, ConstraintException {
        if (!taskRepository.existsById(task.getId())) {
            throw new IllegalArgumentException("Task with id " + task.getId() + " doesn't exist. Please use save to save this instance.");
        }

        Task newTask = setNulLValues(task);

        validateTaskProperties(newTask);

        return taskRepository.save(newTask);
    }

    private Task setNulLValues(Task task) {
        Task taskInDb = taskRepository.findById(task.getId()).orElse(new Task());

        task.setName(task.getName() == null ? taskInDb.getName() : task.getName());

        task.setDescription(task.getDescription() == null ? taskInDb.getDescription() : task.getDescription());

        task.setTimeofcreation(task.getTimeofcreation() == null ? taskInDb.getTimeofcreation() : task.getTimeofcreation());

        //TODO: fix 2 -> null-ra állítás
        task.setMaintaskid(task.getMaintaskid() == null ? taskInDb.getMaintaskid() : task.getMaintaskid());

        task.setOwnerid(task.getOwnerid() == null ? taskInDb.getOwnerid() : task.getOwnerid());

        return task;
    }

    private void validateTaskProperties(Task task) throws NullValueException, ConstraintException {

        String errorMessage = checkForNullProperties(task);
        if (!errorMessage.isEmpty()) {
            throw new NullValueException(errorMessage);
        }

        errorMessage = checkConstraints(task);
        if (!errorMessage.isEmpty()) {
            throw new ConstraintException(errorMessage);
        }

    }

    private String checkForNullProperties(Task task) {
        StringBuilder errorMessage = new StringBuilder();

        if (task.getName() == null) {
            errorMessage.append("name property not set, ");
        }

        if (task.getDescription() == null) {
            errorMessage.append("description property not set, ");
        }

        if (task.getTimeofcreation() == null) {
            errorMessage.append("timeofcreation property not set, ");
        }

        if (task.getOwnerid() == null) {
            errorMessage.append("ownerid property not set, ");
        }

        return errorMessage.toString();
    }

    private String checkConstraints(Task task) {
        StringBuilder errorMessage = new StringBuilder();

        if (task.getMaintaskid() != null && !taskRepository.existsById(task.getMaintaskid())) {
            errorMessage.append("maintaskid is not a valid task's ID, ");
        }

        if (!userRepository.existsById(task.getOwnerid())) {
            errorMessage.append("ownerid is not a valid user's ID, ");
        }

        return errorMessage.toString();
    }

    @Override
    public boolean deleteTask(Long id) {
        try {
            taskRepository.deleteById(id);
        } catch (Exception e) {
            System.out.println("Deletion of " + id + " task failed: " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteAll() {
        try {
            taskRepository.deleteAll();
        } catch (Exception e) {
            System.out.println("Deletion of all tasks failed: " + e.getMessage());
            return false;
        }

        return true;
    }

}
