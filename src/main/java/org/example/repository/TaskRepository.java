package org.example.repository;

import org.example.model.Task;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("taskRepository")
public interface TaskRepository extends CrudRepository<Task, Long> {

    @Modifying
    @Query("UPDATE \"task\" SET maintaskid = :maintaskid WHERE id = :subtaskid")
    boolean assignUnderTask(@Param("subtaskid") Long subtaskId, @Param("maintaskid") Long maintaskId);

    @Query("SELECT * " +
            "FROM \"task\" " +
            "WHERE name LIKE :name")
    List<Task> getTasksByName(@Param("name") String name);

    @Query("SELECT * " +
            "FROM \"task\" " +
            "WHERE ownerid = :ownerid")
    List<Task> getTasksByOwnerId(@Param("ownerid") Long ownerid);

    @Query("SELECT * " +
            "FROM \"task\" " +
            "INNER JOIN \"completedTask\" ON task.id = completedTask.taksid" +
            "WHERE completedTask.userid = :userid")
    List<Task> getCompletedTasksForUser(@Param("userid") Long userid);

}
