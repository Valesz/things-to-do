package org.example.service.impl;

import org.example.repository.TaskRepository;
import org.example.model.Task;
import org.example.service.Filter;
import org.example.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class TaskServiceImpl implements TaskService {

    @Resource(name = "taskRepository")
    private TaskRepository taskRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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

        System.out.println(sb);

        return sb.toString();
    }

    @Override
    public List<Task> getCompletedTasksForUser(Long userId) {
        return taskRepository.getCompletedTasksForUser(userId);
    }

    @Override
    public Task saveTask(Task task) {
        return taskRepository.save(task);
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
