package org.example.service.impl;

import org.example.model.KeywordsForTasks;
import org.example.repository.KeywordsForTasksRepository;
import org.example.service.KeywordsForTasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class KeywordsForTasksServiceImpl implements KeywordsForTasksService {

    @Autowired
    private KeywordsForTasksRepository keywordsForTasksRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Iterable<KeywordsForTasks> getAllKeywordsForTasks() {
        return keywordsForTasksRepository.findAll();
    }

    @Override
    public KeywordsForTasks getKeywordForTaskById(Long id) {
        return keywordsForTasksRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<KeywordsForTasks> getByKeywordsForTasksObject(KeywordsForTasks keywordsForTasks) {
        if (keywordsForTasks == null) {
            return getAllKeywordsForTasks();
        }

        SqlParameterSource namedParams = new MapSqlParameterSource()
                .addValue("id", keywordsForTasks.getId())
                .addValue("taskid", keywordsForTasks.getTaskid())
                .addValue("keyword", keywordsForTasks.getKeyword());

        String query = constructQueryForGetByTaskObject(keywordsForTasks);

        return namedParameterJdbcTemplate.query(query, namedParams, rs -> {
            List<KeywordsForTasks> keywordsForTasksList = new ArrayList<>();

            while (rs.next()) {
                keywordsForTasksList.add(KeywordsForTasks.builder()
                        .id(rs.getLong("id"))
                        .taskid(rs.getLong("taskid"))
                        .keyword(rs.getString("keyword"))
                        .build()
                );
            }

            return keywordsForTasksList;
        });
    }

    private String constructQueryForGetByTaskObject(KeywordsForTasks keywordsForTasks) {
        StringBuilder query = new StringBuilder(" SELECT * FROM \"keywordsfortasks\" ");

        query.append(" WHERE 1 = 1 ");

        if (keywordsForTasks.getId() != null) {
            query.append(" AND id = :id ");
        }

        if (keywordsForTasks.getTaskid() != null) {
            query.append(" AND taskid = :taskid ");
        }

        if (keywordsForTasks.getKeyword() != null) {
            query.append(" AND keyword = :keyword ");
        }

        return query.toString();
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
