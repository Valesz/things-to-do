package org.example.repository;

import org.example.model.Task;
import org.example.model.listing.TaskListingFilter;
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

	@Query(" SELECT TASK.ID, NAME, TASK.DESCRIPTION, TASK.TIMEOFCREATION, MAINTASKID, OWNERID, USERNAME AS OWNERNAME FROM \"task\" TASK "
		+ " INNER JOIN \"user\" USERT ON USERT.ID = TASK.OWNERID "
		+ " ORDER BY TIMEOFCREATION DESC, ID DESC LIMIT :LIMIT OFFSET :OFFSET")
	Iterable<TaskListingFilter> getAllTasksAsListingFilter(@Param("OFFSET") Long offset, @Param("LIMIT") Long limit);
}
