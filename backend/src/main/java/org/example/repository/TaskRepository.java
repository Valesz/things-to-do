package org.example.repository;

import org.example.model.Task;
import org.example.model.TaskListingFilter;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("taskRepository")
public interface TaskRepository extends CrudRepository<Task, Long>
{
	@Modifying
	@Query("UPDATE \"task\" SET MAINTASKID = :MAINTASKID WHERE ID = :ID")
	Integer setMainTaskId(@Param("ID") Long id, @Param("MAINTASKID") Long mainTaskId);

	@Query("SELECT * FROM \"task\" ORDER BY TIMEOFCREATION DESC, ID DESC")
	Iterable<TaskListingFilter> getAllTasksAsListingFilter();
}
