package org.example.service.impl;

import org.example.model.CompletedTask;
import org.example.repository.CompletedTasksRepository;
import org.example.service.CompletedTasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompletedTasksServiceImpl implements CompletedTasksService {

    @Autowired
    private CompletedTasksRepository completedTasksRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public Iterable<CompletedTask> getAllCompletedTasks() {
        return completedTasksRepository.findAll();
    }

    @Override
    public CompletedTask getCompletedTaskById(Long id) {
        return completedTasksRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<CompletedTask> getByCompletedTasksObject(CompletedTask completedTask) {
        if (completedTask == null) {
            return null;
        }

        SqlParameterSource namedParams = new MapSqlParameterSource()
                .addValue("id", completedTask.getId())
                .addValue("userid", completedTask.getUserid())
                .addValue("taskid", completedTask.getTaskid());

        String query = constructQueryByOwnObject(completedTask);

        return namedParameterJdbcTemplate.query(query, namedParams, rs -> {
            List<CompletedTask> completedTaskList = new ArrayList<>();

            while (rs.next()) {
                completedTaskList.add(CompletedTask.builder()
                        .id(rs.getLong("id"))
                        .userid(rs.getLong("userid"))
                        .taskid(rs.getLong("taskid"))
                        .build()
                );
            }

            return completedTaskList;
        });
    }

    private String constructQueryByOwnObject(CompletedTask completedTask) {
        StringBuilder query = new StringBuilder(" SELECT * FROM \"completedTasks\n ");

        if (completedTask.getId() != null) {
            query.append(" WHERE \"id\" = :id ");
        }

        if (completedTask.getUserid() != null) {
            query.append(" WHERE \"userid\" = :userid ");
        }

        if (completedTask.getTaskid() != null) {
            query.append(" WHERE \"taskid\" = :taskid ");
        }

        return query.toString();
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
