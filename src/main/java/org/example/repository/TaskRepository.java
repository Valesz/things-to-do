package org.example.repository;

import org.example.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("taskRepository")
public interface TaskRepository extends CrudRepository<Task, Long> {
}
