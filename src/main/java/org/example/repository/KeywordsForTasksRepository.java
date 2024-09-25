package org.example.repository;

import org.example.model.KeywordsForTasks;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("keywordsForTasksRepository")
public interface KeywordsForTasksRepository extends CrudRepository<KeywordsForTasks, Long> {

    @Modifying
    @Query("DELETE FROM \"keywordsForTasks\" WHERE keyword = :keyword")
    int deleteKeyword(@Param("keyword") String keyword);

}
