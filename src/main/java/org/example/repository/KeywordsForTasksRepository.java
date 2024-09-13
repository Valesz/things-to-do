package org.example.repository;

import org.example.model.KeywordsForTasks;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository("keywordsForTasksRepository")
public interface KeywordsForTasksRepository extends CrudRepository<KeywordsForTasks, Long> {

    @Query("SELECT * FROM \"keywordsForTasks\" WHERE taskid = :taskid ")
    Iterable<KeywordsForTasks> getKeywordsForTaskByTaskId(@Param("taskid") Long taskId);

    @Query("SELECT * FROM \"keywordsForTasks\" WHERE keyword = :keyword")
    Iterable<KeywordsForTasks> getTasksByKeyword(@Param("keyword") String keyword);

    @Query("SELECT * FROM \"keywordsForTasks\" WHERE 1 = 1 AND keyword IN (:keywords)")
    Iterable<KeywordsForTasks> getTasksByKeyword(@Param("keywords") Collection<String> keywords);

    @Query("DELETE FROM \"keywordsForTasks\" WHERE keyword = :keyword")
    void deleteKeyword(@Param("keyword") String keyword);
}
